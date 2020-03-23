#ifndef SUDOKU_H
#define SUDOKU_H

const bool DEBUG_MODE = false;
enum { ROW=9, COL=9, N = 81, NEIGHBOR = 20 };
const int NUM = 9;
const int PNUM = 2000000;

extern char ansBoard[PNUM][N+9];
extern char puzzle[PNUM][128];
/*extern int board[N];
extern int spaces[N];
extern int nspaces;
extern int (*chess)[COL];*/

void init_neighbors();
void input(const char in[N], int board[N], int spaces[N]);

bool solve_sudoku_dancing_links(int unused, int board[N]);
bool solved(int board[N]);
#endif
