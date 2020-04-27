# CXXFLAGS+=-O2 -ggdb -DDEBUG
# CXXFLAGS+=-Wall -Wextra

all: new_version

new_version: main.cc sudoku_basic.cc sudoku_dancing_links.cc
	g++ -O3 -pthread -o $@ $^

clean :
	rm new_version