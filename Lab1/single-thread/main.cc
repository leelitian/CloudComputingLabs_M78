#include <assert.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <sys/time.h>

#include "sudoku.h"

int64_t now()                                       
{
    struct timeval tv;
    gettimeofday(&tv, NULL);                        // 获取当前精确时间
    return tv.tv_sec * 1000000 + tv.tv_usec;
}

int main(int argc, char *argv[])
{
    char file_path[128];

    char puzzle[128];                                 
    int total_solved = 0;                            // 已解决的谜题总数
    int total = 0;                                   // 谜题总数
    bool (*solve)(int) = solve_sudoku_dancing_links; // 使用“舞蹈链”算法解决数独

    // 读取文件名
    char *_ = fgets(file_path, sizeof file_path, stdin);
    if (file_path[strlen(file_path) - 1] == '\n')
        file_path[strlen(file_path) - 1] = '\0';

    int64_t start = now(); // 计时

    FILE *fp = fopen(file_path, "r"); // 打开文件

    while (fgets(puzzle, sizeof puzzle, fp) != NULL)
    {
        if (strlen(puzzle) >= N)
        {
            ++total;
            // board = puzzle
            input(puzzle);

            if (solve(0))
            {
                ++total_solved;
                // 检查结果是否正确
                if (!solved())
                    assert(0);
                // 打印结果
                for (int i = 0; i < N; ++i)
                    printf("%d", board[i]);
                printf("\n");
            }
            else
            {
                printf("No: %s", puzzle);
            }
        }
    }

    int64_t end = now();
    double sec = (end - start) / 1000000.0;
    printf("%f sec %f ms each %d\n", sec, 1000 * sec / total, total_solved);// 输出运行时间

    return 0;
}
