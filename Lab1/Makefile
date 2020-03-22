# CXXFLAGS+=-O2 -ggdb -DDEBUG
# CXXFLAGS+=-Wall -Wextra

# all: sudoku_solve, ./Test_sample_production

sudoku_solve: main.cc sudoku_basic.cc sudoku_dancing_links.cc
	g++ -O3 -pthread -o $@ $^ 

clean :
	rm sudoku_solve