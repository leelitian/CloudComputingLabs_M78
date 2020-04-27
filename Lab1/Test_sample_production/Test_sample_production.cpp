#include <iostream>
#include <fstream>
#include <ctime>
#include <cstring>
using namespace std;

int sudo[9][9], hole[9][9];

bool set(int x, int y, int val)
{
	if (sudo[y][x] != 0) //非空
		return false;
	int x0, y0;
	for (x0 = 0; x0 < 9; x0++)
	{
		if (sudo[y][x0] == val) //行冲突
			return false;
	}
	for (y0 = 0; y0 < 9; y0++)
	{
		if (sudo[y0][x] == val) //列冲突
			return false;
	}
	for (y0 = y / 3 * 3; y0 < y / 3 * 3 + 3; y0++)
	{
		for (x0 = x / 3 * 3; x0 < x / 3 * 3 + 3; x0++)
		{
			if (sudo[y0][x0] == val) //格冲突
				return false;
		}
	}
	sudo[y][x] = val;
	return true;
}

void reset(int x, int y)
{
	sudo[y][x] = 0;
}

void initXOrd(int *xOrd) //0~9随机序列
{
	int i, k, tmp;
	for (i = 0; i < 9; i++)
	{
		xOrd[i] = i;
	}
	for (i = 0; i < 9; i++)
	{
		k = rand() % 9;
		tmp = xOrd[k];
		xOrd[k] = xOrd[i];
		xOrd[i] = tmp;
	}
}

bool fillFrom(int y, int val)
{
	int xOrd[9];
	initXOrd(xOrd); //生成当前行的扫描序列
	for (int i = 0; i < 9; i++)
	{
		int x = xOrd[i];
		if (set(x, y, val))
		{
			if (y == 8) //到了最后一行
			{
				if (val == 9 || fillFrom(0, val + 1)) //当前填9则结束, 否则从第一行填下一个数
					return true;
			}
			else
			{
				if (fillFrom(y + 1, val)) //下一行继续填当前数
					return true;
			}
			reset(x, y); //回溯
		}
	}
	return false;
}

void digHole(int holeCnt)
{
	int idx[81];
	int i, k;
	for (i = 0; i < 81; i++)
	{
		hole[i / 9][i % 9] = 0;
		idx[i] = i;
	}
	for (i = 0; i < holeCnt; i++) //随机挖洞位置
	{
		k = rand() % 81;
		int tmp = idx[k];
		idx[k] = idx[i];
		idx[i] = tmp;
	}
	for (i = 0; i < holeCnt; i++)
	{
		hole[idx[i] / 9][idx[i] % 9] = 1;
	}
}

int main(int argc, char *argv[])
{
	cout << "数据正在生成，请耐心等待3分钟..." << endl;
	srand((unsigned)time(NULL));

	for (int j = 0; j < 11; j++)
	{
		int Sudo_num = (1 << (j + 10));
		string File_name = "test" + to_string(1 << j) + "K.txt";
		ofstream f1;
		f1.open(File_name);
		for (int i = 0; i < Sudo_num; i++)
		{
			memset(sudo, 0, sizeof(sudo));
			memset(hole, 0, sizeof(hole));

			while (!fillFrom(0, 1))
				;
			digHole(64);

			for (int y = 0; y < 9; y++)
				for (int x = 0; x < 9; x++)
					(hole[y][x] == 0) ? (f1 << sudo[y][x]) : (f1 << 0);

			if (i == Sudo_num - 1)
				continue;

			f1 << "\n";
		}
	}
	cout << "测试数据生成完成！"
		 << "\n";

	return 0;
}
