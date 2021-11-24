using Distributions
using DataStructures


@enum EVENT starts=1 ends=2

function customerArrival(pq, q, queueNumber, time, p)
  people = 0.0
  seconds = 0.0
  while people == 0
    people = rand(p)
    seconds += 1
  end
  nextTime = people/seconds
  enqueue!(pq, (queueNumber, nextTime, starts), nextTime)
  enqueue!(q[queueNumber], nextTime)
end

function customerProcessStart(pq, q, queueNumber, time, e)
  nextTime = rand(e) + time
  enqueue!(pq, (queueNumber, nextTime, ends), nextTime)
end

function initProcess(timeline, queues, p)
  for i=1:length(queues)
    customerArrival(timeline, queues, i, 0, p)
  end
end

function runProcess(p, e, n, time)
  currTime = 0
  waitTimeData = []
  startedProcesses = zeros(Int64, n)
  timeline = PriorityQueue()
  queues = [Queue(Any) for x=1:n]
  initProcess(timeline, queues, p)
  while currTime < time
    event = dequeue!(timeline)
    currTime = event[2]
    queueNumber = event[1]
    if event[3] == starts
      customerArrival(timeline, queues, event[1], event[2], p)
      if startedProcesses[queueNumber] == 0
        customerProcessStart(timeline, queues, queueNumber, currTime, e)
        startedProcesses[queueNumber] += 1
      end
    else
      append!(waitTimeData, [currTime - dequeue!(queues[queueNumber])])
      startedProcesses[queueNumber] -= 1
      if startedProcesses[queueNumber] < length(queues[queueNumber])
        customerProcessStart(timeline, queues, queueNumber, currTime, e)
        startedProcesses[queueNumber] += 1
      end
    end
  end
  return waitTimeData
end


println(mean(runProcess(Poisson(0.99), Exponential(1.0), 100, 10000)))
