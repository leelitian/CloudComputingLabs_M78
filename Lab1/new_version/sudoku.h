#ifndef SUDOKU_H
#define SUDOKU_H

const bool DEBUG_MODE = false;
enum { ROW=9, COL=9, N = 81, NEIGHBOR = 20 };
const int NUM = 9;
const int PNUM = 1280000;
const int BATCH = 100;
void init_neighbors();
void input(const char in[N], int board[N], int spaces[N]);

bool solve_sudoku_dancing_links(int board[N]);
bool solved(int board[N]);
#endif
