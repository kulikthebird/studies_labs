package main

import "fmt"

type MessageType int

const (
	Candidate    MessageType = iota
	LeaderAccept MessageType = iota
	Start        MessageType = iota
	Finish       MessageType = iota
)

type Message struct {
	msgType MessageType
	id      int
}

type Node struct {
	nodeId         int
	leaderId       int
	channel        chan Message
	controlChannel chan Message
	nextNeighbor   chan Message
}

func msgHandler(msg Message, node *Node) {
	switch msg.msgType {
	case Candidate:
		if msg.id == node.nodeId {
			node.nextNeighbor <- Message{LeaderAccept, msg.id}
		} else if msg.id > node.leaderId {
			node.leaderId = msg.id
			node.nextNeighbor <- Message{Candidate, msg.id}
		} else {
			node.nextNeighbor <- Message{Candidate, node.nodeId}
		}
	case LeaderAccept:
		if msg.id != node.nodeId {
			node.nextNeighbor <- Message{LeaderAccept, msg.id}
		} else {
			node.controlChannel <- Message{Finish, msg.id}
		}
	case Start:
		node.nextNeighbor <- Message{Candidate, node.nodeId}
	}
}

func msgLoop(node *Node) {
	for true {
		msg := <-node.channel
		msgHandler(msg, node)
	}
}

func addNodeToRing(nodeId int, channel chan Message, nextNeighbor chan Message, controlChannel chan Message) {
	node := Node{nodeId, nodeId, channel, controlChannel, nextNeighbor}
	msgLoop(&node)
}

func main() {
	numberOfNodes := 10
	controlChannel := make(chan Message)
	connections := make([]chan Message, numberOfNodes)
	for i := 0; i < numberOfNodes; i++ {
		connections[i] = make(chan Message)
	}
	for i := 0; i < numberOfNodes; i++ {
		go addNodeToRing(i, connections[i], connections[(i+1)%numberOfNodes], controlChannel)
	}
	connections[numberOfNodes/2] <- Message{Start, 0}
	fmt.Println("Chosen leader: ", <-controlChannel)
}
