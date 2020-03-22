#include <assert.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <sys/time.h>
#include <pthread.h>

#include "sudoku.h"

int cur_t = 0;
int num_t = 0;
int num_per_t = 0;
int total_solved = 0;                            // 已解决的谜题总数
int total = 0;                                   // 谜题总数
bool (*solve)(int, int[]) = solve_sudoku_dancing_links; // 使用“舞蹈链”算法解决数独
pthread_mutex_t mutex;

int64_t now()                                       
{
    struct timeval tv;
    gettimeofday(&tv, NULL);                        // 获取当前精确时间
    return tv.tv_sec * 1000000 + tv.tv_usec;
}

void *sudoku_solve(void *args)
{
    int cur_id = (*((int *)args)) * num_per_t;
    int end_id = cur_id+num_per_t < total ? cur_id+num_per_t : total;
    int board[N];
    int spaces[N];

    // printf("id: %d cur: %d end: %d\n", *((int *)args), cur_id, end_id);

    while (cur_id < end_id)
    {
        // board = puzzle
        input(puzzle[cur_id], board, spaces);

        if (solve(0, board))
        {
            // 临界区
            pthread_mutex_lock(&mutex);
            ++total_solved;
            pthread_mutex_unlock(&mutex);
            // 检查结果是否正确
            if (!solved(board))
                assert(0);
            // 将结果保存至ansboard
            for (int i = 0; i < N; i++)
                ansBoard[cur_id][i] = char('0' + board[i]);
        }
        else
        {
            // printf("No: %s", puzzle);
            /*ansBoard[id][0] = 'N';
            ansBoard[id][1] = 'o';
            ansBoard[id][2] = ':';
            ansBoard[id][3] = ' ';*/
            for (int i = 0; i < N; i++)
                ansBoard[cur_id][i] = puzzle[cur_id][i];
            ansBoard[cur_id][N] = '\0';
        }

        cur_id++;
    }
    
    
}

int main(int argc, char *argv[])
{
    char file_path[128];

    // 读取文件名
    char *_ = fgets(file_path, sizeof file_path, stdin);
    if (file_path[strlen(file_path) - 1] == '\n')
        file_path[strlen(file_path) - 1] = '\0';

    int a = scanf("%d %d", &num_t, &num_per_t);
    a++;

    int64_t start = now(); // 计时

    FILE *fp = fopen(file_path, "r"); // 打开文件

    pthread_t tt[num_t];
    int id[num_t];

    while (fgets(puzzle[total], sizeof puzzle, fp) != NULL)
    {
        if (strlen(puzzle[total]) >= N)
        {
            if (!(++total % num_per_t))
            {
            	// printf("creating...%d\n", cur_t);
            	id[cur_t] = cur_t;
                pthread_create(&tt[cur_t], NULL, sudoku_solve, (void*)&id[cur_t]);
                cur_t++;
            }
        }
    }

    if (total % num_per_t)
    {
    	// printf("surcreating...%d\n", cur_t);
    	id[cur_t] = cur_t;
    	pthread_create(&tt[cur_t], NULL, sudoku_solve, (void*)&id[cur_t]);
    }

    for (int i = 0; i < num_t; i++)
        pthread_join(tt[i], NULL);

    for (int i = 0; i < total; i++)
         printf("%s\n", ansBoard[i]);

    int64_t end = now();
    double sec = (end - start) / 1000000.0;
    printf("%f sec %f ms each %d\n", sec, 1000 * sec / total, total_solved);// 输出运行时间

    return 0;
}

