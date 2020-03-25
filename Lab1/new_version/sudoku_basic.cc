#include <assert.h>
#include <stdio.h>

#include <algorithm>

#include "sudoku.h"
#define PNUM 1280000

static void find_spaces(int board[N], int spaces[N])
{
	int nspaces = 0;
	for (int cell = 0; cell < N; ++cell)
	{
		if (board[cell] == 0)
			spaces[nspaces++] = cell;
	}
}

// 将puzzle写入board
void input(const char in[N], int board[N], int spaces[N])
{
	for (int cell = 0; cell < N; ++cell)
	{
		board[cell] = in[cell] - '0';
		assert(0 <= board[cell] && board[cell] <= NUM);
	}
	find_spaces(board, spaces);
}

// 检查board是否为正确解
bool solved(int board[N])
{
	int(*chess)[COL] = (int(*)[COL])board;
	for (int row = 0; row < ROW; ++row)
	{
		// check row
		int occurs[10] = {0};
		for (int col = 0; col < COL; ++col)
		{
			int val = chess[row][col];
			assert(1 <= val && val <= NUM);
			++occurs[val];
		}

		if (std::count(occurs, occurs + 10, 1) != NUM) // 同一行的方格都已经填充了数字
			return false;
	}

	for (int col = 0; col < COL; ++col)
	{
		int occurs[10] = {0};
		for (int row = 0; row < ROW; ++row)
		{
			int val = chess[row][col];
			// assert(1 <= val && val <= NUM);
			++occurs[val];
		}

		if (std::count(occurs, occurs + 10, 1) != NUM)
			return false;
	}

	for (int row = 0; row < ROW; row += 3) // 每次查看一个3*3方格
	{
		for (int col = 0; col < COL; col += 3)
		{
			int occurs[10] = {0};
			++occurs[chess[row][col]];
			++occurs[chess[row][col + 1]];
			++occurs[chess[row][col + 2]];
			++occurs[chess[row + 1][col]];
			++occurs[chess[row + 1][col + 1]];
			++occurs[chess[row + 1][col + 2]];
			++occurs[chess[row + 2][col]];
			++occurs[chess[row + 2][col + 1]];
			++occurs[chess[row + 2][col + 2]];

			if (std::count(occurs, occurs + 10, 1) != NUM)
				return false;
		}
	}
	return true;
}
