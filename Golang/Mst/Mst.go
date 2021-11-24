package main

import (
	"fmt"
	"time"

	"./Graph"
)

const MaxUint = ^uint(0)
const MaxInt = int(MaxUint >> 1)

type NodeState int

const (
	Sleeping NodeState = iota
	Find     NodeState = iota
	Found    NodeState = iota
)

type EdgeState int

const (
	Basic    EdgeState = iota
	Branch   EdgeState = iota
	Rejected EdgeState = iota
)

type MessageType int

const (
	Awaking       MessageType = iota
	Connect       MessageType = iota
	Initiate      MessageType = iota
	Test          MessageType = iota
	Accept        MessageType = iota
	Reject        MessageType = iota
	Report        MessageType = iota
	ChangeCore    MessageType = iota
	Break         MessageType = iota
	ShowNeighbors MessageType = iota
)

type Message struct {
	MsgType    MessageType
	Parameter1 int
	Parameter2 int
	Parameter3 int
	Source     int
}
type Node struct {
	id             int
	levelNumber    int
	fragmentNumber int
	findCount      int
	bestWeight     int
	bestEdge       *Edge
	inBranch       *Edge
	testEdge       *Edge
	state          NodeState
	channel        chan interface{}
	controlChannel chan Message
	neighbors      Edges
}
type Edge struct {
	neighbor Graph.Neighbor
	state    EdgeState
}
type Edges []*Edge
type Job struct {
	msg        Message
	sourceNode *Edge
}

func getMessageString(msgType MessageType) string {
	switch msgType {
	case Awaking:
		return "Awaking"
	case Connect:
		return "Connect"
	case Initiate:
		return "Initiate"
	case Test:
		return "Test"
	case Accept:
		return "Accept"
	case Reject:
		return "Reject"
	case Report:
		return "Report"
	case ChangeCore:
		return "ChangeCore"
	}
	return "Unknown"
}
func sendMessage(neighbor Graph.Neighbor, msg Message) {
	fmt.Println("Sending message", getMessageString(msg.MsgType), "from node", msg.Source, "to node", neighbor.NeighborId, "with params: (",
		msg.Parameter1, ",", msg.Parameter2, ",", msg.Parameter3, ")")
	neighbor.Channel <- msg
}
func postponeMessage(node *Node, msg Message) {
	node.channel <- msg
}
func msgLoop(node *Node) {
	for true {
		msg := <-node.channel
		msgHandler(msg.(Message), node)
	}
}
func getSourceEdge(node *Node, id int) *Edge {
	for _, edge := range node.neighbors {
		if edge.neighbor.NeighborId == id {
			return edge
		}
	}
	return nil
}
func nodeFunction(controlChannel chan Message, nodeB Graph.NodeB, nodeId int) {
	edges := make(Edges, len(nodeB.Neighbors))
	for i, neighbor := range nodeB.Neighbors {
		edges[i] = new(Edge)
		*edges[i] = Edge{neighbor, Basic}
	}
	node := Node{}
	node.id = nodeId
	node.channel = nodeB.Channel
	node.neighbors = edges
	node.controlChannel = controlChannel
	msgLoop(&node)
}

func minimumNeighbor(neighbors Edges) *Edge {
	if len(neighbors) == 0 {
		return nil
	}
	var minimum *Edge = neighbors[0]
	for _, elem := range neighbors {
		if minimum.neighbor.Weight > elem.neighbor.Weight {
			minimum = elem
		} else if minimum.neighbor.Weight == elem.neighbor.Weight {
			if minimum.neighbor.NeighborId < elem.neighbor.NeighborId {
				minimum = elem
			}
		}
	}
	return minimum
}
func wakeUp(node *Node, neighbors Edges) {
	m := minimumNeighbor(neighbors)
	m.state = Branch
	fmt.Println("Changed ", m.neighbor.NeighborId, "state in", node.id)
	node.levelNumber = 0
	node.state = Found
	node.findCount = 0
	sendMessage(m.neighbor, Message{Connect, 0, 0, 0, node.id})
}
func test(node *Node) {
	basicEdges := make(Edges, 0)
	for _, edge := range node.neighbors {
		if edge.state == Basic {
			basicEdges = append(basicEdges, edge)
		}
	}
	node.testEdge = minimumNeighbor(basicEdges)
	if node.testEdge != nil {
		sendMessage(node.testEdge.neighbor, Message{Test, node.levelNumber, node.fragmentNumber, 0, node.id})
	} else {
		report(node)
	}
}
func report(node *Node) {
	if node.findCount == 0 && node.testEdge == nil {
		node.state = Found
		sendMessage(node.inBranch.neighbor, Message{Report, node.bestWeight, 0, 0, node.id})
	}
}
func changeCore(node *Node) {
	if node.bestEdge.state == Branch {
		sendMessage(node.bestEdge.neighbor, Message{ChangeCore, 0, 0, 0, node.id})
	} else {
		sendMessage(node.bestEdge.neighbor, Message{Connect, node.levelNumber, 0, 0, node.id})
		node.bestEdge.state = Branch
		fmt.Println("Changed ", node.bestEdge.neighbor.NeighborId, "state in", node.id)
	}
}
func msgHandler(msg Message, node *Node) {
	var sourceNodeCh *Edge = getSourceEdge(node, msg.Source)
	switch msg.MsgType {
	case Awaking:
		wakeUp(node, node.neighbors)
	case Connect:
		if node.state == Sleeping {
			wakeUp(node, node.neighbors)
		}
		if msg.Parameter1 < node.levelNumber {
			sourceNodeCh.state = Branch
			fmt.Println("Changed after connect ", sourceNodeCh.neighbor.NeighborId, "state in", node.id)
			sendMessage(sourceNodeCh.neighbor, Message{Initiate, node.levelNumber, node.fragmentNumber, int(node.state), node.id})
			if node.state == Find {
				node.findCount++
			}
		} else if sourceNodeCh.state == Basic {
			postponeMessage(node, msg)
		} else {
			sendMessage(sourceNodeCh.neighbor, Message{Initiate, node.levelNumber + 1, sourceNodeCh.neighbor.Weight, int(Find), node.id})
		}
	case Initiate:
		node.levelNumber = msg.Parameter1
		node.fragmentNumber = msg.Parameter2
		node.state = NodeState(msg.Parameter3)
		node.inBranch = sourceNodeCh
		node.bestWeight = MaxInt
		node.bestEdge = nil
		for _, edge := range node.neighbors {
			if edge.state == Branch && edge.neighbor.NeighborId != sourceNodeCh.neighbor.NeighborId {
				msg.Source = node.id
				sendMessage(edge.neighbor, msg)
				if NodeState(msg.Parameter3) == Find {
					node.findCount++
				}
			}
		}
		if NodeState(msg.Parameter3) == Find {
			test(node)
		}
	case Test:
		if node.state == Sleeping {
			wakeUp(node, node.neighbors)
		}
		if msg.Parameter1 > node.levelNumber {
			postponeMessage(node, msg)
		} else if msg.Parameter2 != node.fragmentNumber {
			sendMessage(sourceNodeCh.neighbor, Message{Accept, 0, 0, 0, node.id})
		} else {
			if sourceNodeCh.state == Basic {
				sourceNodeCh.state = Rejected
			}
			if node.testEdge == nil || node.testEdge.neighbor.NeighborId != sourceNodeCh.neighbor.NeighborId {
				sendMessage(sourceNodeCh.neighbor, Message{Reject, 0, 0, 0, node.id})
			} else {
				test(node)
			}
		}
	case Accept:
		node.testEdge = nil
		if sourceNodeCh.neighbor.Weight < node.bestWeight {
			node.bestEdge = sourceNodeCh
			node.bestWeight = sourceNodeCh.neighbor.Weight
		}
		report(node)
	case Reject:
		if sourceNodeCh.state == Basic {
			sourceNodeCh.state = Rejected
		}
		test(node)
	case Report:
		if sourceNodeCh.neighbor.NeighborId != node.inBranch.neighbor.NeighborId {
			node.findCount--
			if msg.Parameter1 < node.bestWeight {
				node.bestEdge = sourceNodeCh
				node.bestWeight = msg.Parameter1
			}
			report(node)
		} else if node.state == Find {
			postponeMessage(node, msg)
		} else if msg.Parameter1 > node.bestWeight {
			changeCore(node)
		} else if msg.Parameter1 == MaxInt && node.bestWeight == MaxInt {
			fmt.Println("Node", node.id, "indicates end of the algorithm")
			node.controlChannel <- Message{}
		}
	case ChangeCore:
		changeCore(node)
	case ShowNeighbors:
		neighbors := make([]int, 0)
		for _, edge := range node.neighbors {
			if edge.state == Branch {
				neighbors = append(neighbors, edge.neighbor.NeighborId)
			}
		}
		fmt.Println("Node", node.id, "neighbors in MST:", neighbors)
	}
}

func main() {
	nodes := Graph.GetGraphFromFile("graph.g")
	controlChannel := make(chan Message, 255)
	for i := 0; i < len(nodes); i++ {
		go nodeFunction(controlChannel, nodes[i], i)
	}
	nodes[0].Channel <- Message{Awaking, 0, 0, 0, -1}
	<-controlChannel
	for _, ch := range nodes {
		ch.Channel <- Message{ShowNeighbors, 0, 0, 0, -1}
	}
	<-time.After(time.Millisecond * 1000)
}
