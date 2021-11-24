using PyCall
using JuMP
@pyimport matplotlib.pyplot as pyplot


function drawGantt(b::Array{JuMP.Variable, 1}, c::Array{JuMP.Variable, 1}, m=1:length(b))
  beginnings = map(x -> Int(x), getvalue(b))
  ends = map(x -> Int(x), getvalue(c))
  pyplot.hlines(0, 0, 0)
  pyplot.hlines(reduce(max,m)+1, 0, 0)
  for i in 1:length(b)
    pyplot.hlines(m[i], beginnings[i], ends[i], linewidth=10.0, color=(rand(), rand(), rand()) )
    pyplot.text((beginnings[i]+ends[i]) / 2, m[i]+0.15, "$(i)", size=25)
  end
  pyplot.show()
end
