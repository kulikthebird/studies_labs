using Primes
using PyCall
using JuMP
using GLPKMathProgInterface
@pyimport liblll as LatticeModule



function generateKeys(n::Int)
  knapsack = [BigInt(rand(1:16))]
  currentSum = knapsack[1]
  for i=2:n
    append!(knapsack, [BigInt(rand(currentSum:2*currentSum))] )
    currentSum += knapsack[end]
  end
  q = reduce(+, knapsack) + 1
  r = q - 1
  while gcd(r, q) != 1
    r -= 1
  end
  beta = [(w*r) % q for w in knapsack]
  return Dict("public" => beta, "private" => (knapsack, q, r))
end

function encrypt(input::Array{UInt8, 1}, publicKey::Array{BigInt, 1})
  inputBits = reduce((x,y) -> string(x,y), map(x -> bin(x, 8), input))
  return reduce(+, [x[2] for x in zip(inputBits, publicKey) if x[1] == '1'])
end

function decrypt(input::BigInt, privateKey::Tuple{Array{BigInt, 1}, BigInt, BigInt})
  s = invmod(privateKey[3], privateKey[2])
  c = (input*s) % privateKey[2]
  w = privateKey[1]
  outputBits = ""
  for i=length(w):-1:1
    if w[i] > c
      outputBits = string("0", outputBits)
    else
      outputBits = string("1", outputBits)
      c -= w[i]
    end
  end
  if c == 0
    return outputBits
  end
  return nothing
end

function lllMethod(encrypted::BigInt, publicKey::Array{BigInt, 1})
  M = eye(BigInt, n)
  M = vcat(M, reshape(publicKey, 1, n))
  M = hcat(M, zeros(BigInt, n+1))
  M[n+1,n+1] = -encrypted

  lattice = LatticeModule.lll_reduction(M)
  column = 0
  for i=1:size(lattice)[1]
    found = true
    for j=1:length(lattice[:,i])
      if lattice[j,i] != 0 && lattice[j,i] != 1
        found = false
        break
      end
    end
    if found == true
      column = i
      break
    end
  end
  return lattice[:,column][1:8]
end

function solveLpProblem(lowerBound,
                        upperBound,
                        g,
                        B,
                        publicKey)
    model = Model(solver = GLPKSolverMIP())
    @variable(model, x[1:g], category = :Int)
    @constraint(model, lowerBound <= x[1] <= upperBound)
    for i=2:g
      @constraint(model, (x[i]*publicKey[1] - x[1]*publicKey[i]) <= B)
      @constraint(model, (x[i]*publicKey[1] - x[1]*publicKey[i]) >= -B)
    end
    if solve(model, suppress_warnings=true) == :Optimal
      return getvalue(x[1])
    end
    return nothing
end

function recursive(g, B, bound, publicKey)
  newBound1 = solveLpProblem(1, bound-1, g, B, publicKey)
  newBound2 = solveLpProblem(bound+1, B-1, g, B, publicKey)
  result = []
  if newBound1 != nothing
    append!(result, [newBound1])
  end
  if newBound2 != nothing
    append!(result, [newBound2])
  end
  return result
end

function shamirMethod(encrypted::BigInt, publicKey::Array{BigInt, 1})
  # step 1
  n = length(publicKey)
  estimatedM = maximum(publicKey)
  estimatedD = (1/n) * log2((n^2) * estimatedM)
  g = Int(maximum([estimatedD+2, 5]))
  B = 2.0^(-n+g) * estimatedM

  # step2
  result = [solveLpProblem(1, B-1, g, B, publicKey)]
  for i=1:Int(ceil(n*log2(n)))
    if i>length(result)
      break
    end
    append!(result, recursive(g, B, result[i], publicKey))
  end

  # step3
  for i=1:minimum([length(result), length(publicKey)])
    for j=1:n^7
      phi = Rational(result[i]/publicKey[i] + j*(1/((n^7)*(2^n)*estimatedM)))
      W = num(phi)
      M = den(phi)
      _W = invmod(W, M)
      decrypted = decrypt(encrypted, ( (publicKey * _W) % M, M, W))
      if decrypted != nothing
        return decrypted
      end
    end
  end
end



n = 8
input = 51
key = generateKeys(n)
encrypted = encrypt([UInt8(input)], key["public"])
println(key["private"][2] / key["private"][3])
println(encrypted)
println(decrypt(encrypted, key["private"]))
println(lllMethod(encrypted, key["public"]))
println(shamirMethod(encrypted, key["public"]))
