#####################################################
# Tomasz Kulik
#####################################################

using JuMP
using GLPKMathProgInterface

dayDictionary = Dict("Monday"=>1, "Tuesday"=>2, "Wednesday"=>3, "Thursday"=>4, "Friday"=>5)
subjectDictionary = Dict("Algebra"=>1, "Analiza"=>2, "Fizyka"=>3, "Chemia mineralna"=>4, "Chemia organiczna"=>5, "Trening"=>6)

type Lecture
  function Lecture(subject::String, groupId::Int64, day::String, startHour::Tuple{Int64,Int64}, endHour::Tuple{Int64,Int64}, mark::Int64)
    new(subject, groupId, day, startHour, endHour, mark)
  end
  subject::String
  groupId::Int64
  day::String
  startHour::Tuple{Int64,Int64}
  endHour::Tuple{Int64,Int64}
  mark::Int64
end

#{przedmiot, grupa, dzień tygodnia, (godzina startu, minuty st.), (godzina zakończenia, minuty zak.)}, ocena
classesSchedule = [
Lecture("Algebra", 1, "Monday", (13, 00), (15, 00), 5),
Lecture("Algebra", 2, "Tuesday", (10, 00), (12, 00), 4),
Lecture("Algebra", 3, "Wednesday", (10, 00), (12, 00), 10),
Lecture("Algebra", 4, "Wednesday", (11, 00), (13, 00), 5),

Lecture("Analiza", 1, "Monday", (13, 00), (15, 00), 4),
Lecture("Analiza", 2, "Tuesday", (10, 00), (12, 00), 4),
Lecture("Analiza", 3, "Wednesday", (11, 00), (13, 00), 5),
Lecture("Analiza", 4, "Thursday", (8, 00), (10, 00), 6),

Lecture("Fizyka", 1, "Tuesday", (8, 00), (11, 00), 3),
Lecture("Fizyka", 2, "Tuesday", (10, 00), (13, 00), 5),
Lecture("Fizyka", 3, "Thursday", (15, 00), (18, 00), 7),
Lecture("Fizyka", 4, "Thursday", (17, 00), (20, 00), 8),

Lecture("Chemia mineralna", 1, "Monday", (8, 00), (10, 00), 10),
Lecture("Chemia mineralna", 2, "Monday", (8, 00), (10, 00), 10),
Lecture("Chemia mineralna", 3, "Thursday", (13, 00), (15, 00), 7),
Lecture("Chemia mineralna", 4, "Friday", (13, 00), (15, 00), 4),

Lecture("Chemia organiczna", 1, "Monday", (9, 00), (10, 30), 0),
Lecture("Chemia organiczna", 2, "Monday", (10, 30), (12, 00), 5),
Lecture("Chemia organiczna", 3, "Friday", (11, 00), (12, 30), 3),
Lecture("Chemia organiczna", 4, "Friday", (13, 00), (14, 30), 4),

Lecture("Trening", 1, "Monday", (13, 00), (15, 00), 11),
Lecture("Trening", 2, "Wednesday", (11, 00), (13, 00), 11),
Lecture("Trening", 3, "Wednesday", (13, 00), (15, 00), 11)
]


function buildCollisionDetectorConstraints(schedule::Array{Lecture,1}, model::JuMP.Model, classesVarsDictionary::Dict)
  n = size(schedule)[1]
  for i=1:n, j=i+1:n
    startI = schedule[i].startHour[1]*100 + schedule[i].startHour[2]
    startJ = schedule[j].startHour[1]*100 + schedule[j].startHour[2]
    endI = schedule[i].endHour[1]*100 + schedule[i].endHour[2]
    endJ = schedule[j].endHour[1]*100 + schedule[j].endHour[2]
    maxStart = max(startI, startJ)
    minEnd = min(endI, endJ)
    if schedule[i].day == schedule[j].day
      if (maxStart < minEnd) || (minEnd >= 1200 && minEnd <= 1400 && maxStart >= 1200 && maxStart <= 1400 && (maxStart - minEnd < 100))
        @constraint(model, classesVarsDictionary[schedule[i].subject][schedule[i].groupId] + classesVarsDictionary[schedule[j].subject][schedule[j].groupId] <= 1 )
      end
    end
  end
  for subjects in classesVarsDictionary
    if subjects[1] != "Trening"
      @constraint(model, sum(values(subjects[2])) == 1)
    end
  end
end

function buildClassVariables(schedule::Array{Lecture,1}, model::JuMP.Model, classesVarsDictionary::Dict)
  n = size(schedule)[1]
  for i=1:n
    if !in(schedule[i].subject, keys(classesVarsDictionary))
      classesVarsDictionary[schedule[i].subject] = Dict()
    end
    classesVarsDictionary[schedule[i].subject][schedule[i].groupId] = @variable(model,category=:Bin, basename="$(schedule[i].subject)#$(schedule[i].groupId)")
  end
end

function buildObjective(schedule::Array{Lecture,1}, model::JuMP.Model, classesVarsDictionary::Dict)
  n = size(schedule)[1]
  varsVector = []
  marksVector = []
  for i=1:n
    append!(varsVector, [classesVarsDictionary[schedule[i].subject][schedule[i].groupId]])
    append!(marksVector, [schedule[i].mark])
  end
  @objective(model, Max, vecdot(varsVector, marksVector))
  return varsVector
end

function buildAndSolveModel(schedule::Array{Lecture,1}, model::JuMP.Model)
  lectureCollisionWithTrainings = Dict()
  classesVarsDictionary = Dict()

  buildClassVariables(schedule, model, classesVarsDictionary)
  buildCollisionDetectorConstraints(schedule, model, classesVarsDictionary)
  varsVector = buildObjective(schedule, model, classesVarsDictionary)
  solve(model, suppress_warnings=true)
  println(model)
  chosenClasses = filter(x-> getvalue(x) == 1.0, varsVector)
  println(getobjectivevalue(model), "\n", chosenClasses)
end

model = Model(solver = GLPKSolverMIP())
buildAndSolveModel(classesSchedule, model)
