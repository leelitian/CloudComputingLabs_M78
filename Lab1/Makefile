CXXFLAGS+=-O2 -ggdb -DDEBUG
CXXFLAGS+=-Wall -Wextra

all: sudoku

sudoku: main.cc sudoku_basic.cc sudoku_dancing_links.cc
	g++ -O2 -o $@ $^
