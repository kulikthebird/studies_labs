package main

import (
	"fmt"
)

type Mutex struct {
	entering     []bool
	number       []int
	numOfThreads int
	ptr          *[]int
}

func createBakery(numOfThreads int, ptr *[]int) Mutex {
	var bakery Mutex
	bakery.numOfThreads = numOfThreads
	bakery.entering = make([]bool, numOfThreads)
	bakery.number = make([]int, numOfThreads)
	bakery.ptr = ptr
	return bakery
}

func (bakery Mutex) lock(i int) {
	bakery.entering[i] = true
	max := 0
	for _, e := range bakery.number {
		if e > max {
			max = e
		}
	}
	bakery.number[i] = max + 1
	bakery.entering[i] = false
	for j := 0; j < bakery.numOfThreads; j++ {
		for bakery.entering[j] {
		}
		for bakery.number[j] != 0 && (bakery.number[j] < bakery.number[i] ||
			(bakery.number[j] == bakery.number[i] && j < i)) {
		}
	}
}

func (bakery Mutex) unlock(i int) {
	bakery.number[i] = 0
}

func Thread(id int, bakery Mutex, channel chan int) {
	bakery.lock(id)
	(*bakery.ptr)[0]++
	for i := 0; i < 1000000; i++ {
	}
	(*bakery.ptr)[(*bakery.ptr)[0]]++
	bakery.unlock(id)
	channel <- 1
}

func main() {
	num := 1000
	tab := make([]int, num+1)
	chans := make([]chan int, num)
	bakery := createBakery(num, &tab)
	for i := 0; i < num; i++ {
		chans[i] = make(chan int)
		go Thread(i, bakery, chans[i])
	}
	for i := 0; i < num; i++ {
		<-chans[i]
	}
	if tab[0] != num {
		fmt.Println("Cos poszło nie tak - tab[0]")
		return
	}
	for i := 0; i < num; i++ {
		if tab[i+1] == 0 {
			fmt.Println("Cos poszło nie tak - tab[", i+1, "]")
			return
		}
	}
	fmt.Println("Wszystko Ok")
}
