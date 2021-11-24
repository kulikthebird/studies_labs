package Graph

import (
    "bufio"
    "strconv"
    "strings"
    "fmt"
    "os"
)

type Neighbor struct {
    NeighborId int
    Weight int
    Channel chan interface{}
}
type Neighbors []Neighbor
type NodeB struct {
    Neighbors Neighbors
    Channel chan interface{}
}
type Nodes []NodeB

func GetGraphFromFile(path string) Nodes {
    inFile, err := os.Open(path)
    defer inFile.Close()
    if err != nil {
        fmt.Println("Couldn't open file: ", path)
        return Nodes{}
    }
    scanner := bufio.NewScanner(inFile)
    scanner.Split(bufio.ScanLines)
    scanner.Scan()
    numOfNodes, _ := strconv.Atoi(scanner.Text())
    nodes := make(Nodes, numOfNodes)
    for i, _ := range nodes {
        nodes[i].Channel = make(chan interface{}, 255)
    }
    for true{
        scanner.Scan()
        if scanner.Text() == "#" {
            break
        }
        var oneLine []string = strings.Split(scanner.Text(), " ")
        if len(oneLine) != 3 {
            fmt.Println("Wrong number of parameter for the edge")
            return Nodes{}
        }
        firstNodeId, _ := strconv.Atoi(oneLine[0])
        secondNodeId, _ := strconv.Atoi(oneLine[1])
        weight, _ := strconv.Atoi(oneLine[2])
        if firstNodeId > secondNodeId {
            fmt.Println("First node should have greater ID than the second")
            return Nodes{}
        }
        nodes[firstNodeId].Neighbors = append(nodes[firstNodeId].Neighbors, Neighbor{secondNodeId, weight, nodes[secondNodeId].Channel})
        nodes[secondNodeId].Neighbors = append(nodes[secondNodeId].Neighbors, Neighbor{firstNodeId, weight, nodes[firstNodeId].Channel})
    }
    return nodes
}
