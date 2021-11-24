using JuMP
using GLPKMathProgInterface


xSize = 10
ySize = 10
paths = [
            [(3, 1), (3, 9)],
            [(2, 1), (4, 2)],
            [(1, 1), (10, 10)],
            [(10, 1), (3, 4)],
            [(10, 5), (3, 5)],
            [(3, 8), (9, 5)]
        ]



model = Model(solver = GLPKSolverMIP())
productSize = xSize * ySize
graphsArray = Array{Array{JuMP.Variable, 2}, 1}()
pathsVariables = Array{Array{JuMP.Variable, 2}, 1}()
pathsNum = length(paths)
for i=1:pathsNum
    capa = Array{JuMP.Variable}(xSize, ySize)
    for x=1:xSize
        for y=1:ySize
            capa[x, y] = @variable(model, basename="capa($(x), $(y))", lowerbound = 0.0, upperbound=1.0)
        end
    end
    append!(pathsVariables, [capa])

    graph = Array{JuMP.Variable}(productSize, productSize)
    for x=1:productSize
        for y=1:productSize
            graph[x, y] = @variable(model, category=:Bin, basename="A($(x), $(y))", lowerbound = 0.0, upperbound=1.0)
        end
    end
    append!(graphsArray, [graph])
end

function getListOfInputNeighbors(graph, x, y)
    input = []
    if x>1
        append!(input, [graph[(y-1)*xSize + x-1, (y-1)*xSize + x]])
    end
    if x<xSize
        append!(input, [graph[(y-1)*xSize + x+1, (y-1)*xSize + x]])
    end
    if y>1
        append!(input, [graph[(y-2)*xSize + x, (y-1)*xSize + x]])
    end
    if y<ySize
        append!(input, [graph[y*xSize + x, (y-1)*xSize + x]])
    end
    return input
end

function getListOfOutputNeighbors(graph, x, y)
    output = []
    if x>1
        append!(output, [graph[(y-1)*xSize + x, (y-1)*xSize + x-1]])
    end
    if x<xSize
        append!(output, [graph[(y-1)*xSize + x, (y-1)*xSize + x+1]])
    end
    if y>1
        append!(output, [graph[(y-1)*xSize + x, (y-2)*xSize + x]])
    end
    if y<ySize
        append!(output, [graph[(y-1)*xSize + x, y*xSize + x]])
    end
    return output
end

for x=1:xSize
    for y=1:ySize
        listOfInput = [getListOfInputNeighbors(g, x, y) for g in graphsArray]
        listOfOutput = [getListOfOutputNeighbors(g, x, y) for g in graphsArray]
        anySource = (x, y) in [p[1] for p in paths]? 1:0
        @constraint(model, anySource + sum(reduce(vcat, listOfInput)) <= 1.0)
        for i=1:pathsNum
            ithSource = (x, y) == paths[i][1]? 1:0
            ithTarget = (x, y) == paths[i][2]? 1:0
            @constraint(model, ithSource + sum(listOfInput[i]) == ithTarget + sum(listOfOutput[i]))
            @constraint(model, ithSource + sum(listOfInput[i]) == pathsVariables[i][x,y])
        end
    end
end

@objective(model, Min, sum(sum(pathsVariables)))
println("Start solving")
solve(model)
println("Solved!")
result = map(Int, map(round, sum([getvalue(pathsVariables[i])' * i for i=1:length(pathsVariables)]) ))

color = [:white, :red, :green, :blue, :yellow, :cyan, :light_red]
for x=1:xSize
    for y=1:ySize
        print_with_color(color[result[x, y] + 1], result[x, y])
    end
    print("\n")
end
