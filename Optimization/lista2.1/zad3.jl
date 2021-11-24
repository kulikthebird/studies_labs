#####################################################
# Tomasz Kulik
#####################################################

using JuMP
using GLPKMathProgInterface

destilEffPetrol1 = 0.15
destilEffOil1 = 0.40
destilEffDestillate1 = 0.15
destilEffRemains1 = 0.15

destilEffPetrol2 = 0.10
destilEffOil2 = 0.35
destilEffDestillate2 = 0.20
destilEffRemains2 = 0.25

crackUnitEffPetrol = 0.50
crackUnitEffOil = 0.20
crackUnitEffRemains = 0.06

sulfurInDestillatedOil1 = 0.2
sulfurInDestillatedOil2 = 1.2
sulfurInCrackedOil1 = 0.3
sulfurInCrackedOil2 = 2.5

expectedSulfurInHomeOil = 0.5

model = Model(solver = GLPKSolverLP())

@variable(model, B1 >= 0)
@variable(model, B2 >= 0)
@variable(model, Destillate1 >= 0)
@variable(model, Destillate2 >= 0)
@variable(model, Oil1 >= 0)
@variable(model, Oil2 >= 0)
@variable(model, Destillate1heavy >= 0)
@variable(model, Destillate2heavy >= 0)
@variable(model, Oil1heavy >= 0)
@variable(model, Oil2heavy >= 0)


@constraint(model, destilEffPetrol1*B1 + destilEffPetrol2*B2 + crackUnitEffPetrol*Destillate1 + crackUnitEffPetrol*Destillate2 >= 200000)
@constraint(model, crackUnitEffOil*Destillate1 + crackUnitEffOil*Destillate2 + Oil1 + Oil2 >= 400000)
@constraint(model, destilEffRemains1*B1 + destilEffRemains2*B2 + crackUnitEffRemains*Destillate1 + crackUnitEffRemains*Destillate2 + Destillate1heavy + Destillate2heavy + Oil1heavy + destilEffOil2*Oil2heavy >= 250000)
@constraint(model, crackUnitEffOil*(sulfurInCrackedOil1-expectedSulfurInHomeOil)*Destillate1 +
    crackUnitEffOil*(sulfurInCrackedOil2-expectedSulfurInHomeOil)*Destillate2 + (sulfurInDestillatedOil1-expectedSulfurInHomeOil)*Oil1 +
      (sulfurInDestillatedOil2-expectedSulfurInHomeOil)*Oil2 <= 0)

@constraint(model, destilEffDestillate1*B1 == Destillate1 + Destillate1heavy)
@constraint(model, destilEffDestillate1*B2 == Destillate2 + Destillate2heavy)
@constraint(model, destilEffOil1*B1 == Oil1 + Oil1heavy)
@constraint(model, destilEffOil1*B2 == Oil2 + Oil2heavy)

@objective(model, Min, B1*1310.0 + B2*1510.0 + Destillate1*20.0 + Destillate2*20.0)

println(model)
solve(model, suppress_warnings=true)

println(getvalue(B1), "\n", getvalue(B2), "\n", getvalue(Destillate1), "\n", getvalue(Destillate2), "\n",
      getvalue(Oil1), "\n", getvalue(Oil2), "\n", getvalue(Destillate1heavy), "\n", getvalue(Destillate2heavy), "\n",
      getvalue(Oil1heavy), "\n", getvalue(Oil2heavy))

println(getobjectivevalue(model))
