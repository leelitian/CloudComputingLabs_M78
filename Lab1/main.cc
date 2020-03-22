#include <assert.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <sys/time.h>
#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/sysinfo.h>

#include "sudoku.h"

int num_t = get_nprocs_conf(); // 获取CPU核心数作为线程数
int thread_data[PNUM];
int total_solved = 0;                                   // 已解决的谜题总数
int total = 0;                                          // 已经读取的谜题数
int tot_t = -1;                                         // zongshu
int cur_t = 0;                                          // 当前的谜题序号
bool shutdown = false;                                  //
bool (*solve)(int, int[]) = solve_sudoku_dancing_links; // 使用“舞蹈链”算法解决数独
pthread_t tt[PNUM];
pthread_mutex_t inc_mutex;
pthread_mutex_t pnc_mutex;

int64_t now()
{
    struct timeval tv;
    gettimeofday(&tv, NULL); // 获取当前精确时间
    return tv.tv_sec * 1000000 + tv.tv_usec;
}

int receive_job()
{
    int ret = -1;
    pthread_mutex_lock(&pnc_mutex);
    if (cur_t > total - 1)
    {
        pthread_mutex_unlock(&pnc_mutex);
        return -1;
    }

    if ((ret = cur_t++) == tot_t - 1)
    {
        shutdown = true;
        // printf("I'm done\n");
    }
    pthread_mutex_unlock(&pnc_mutex);
    return ret;
}

void *sudoku_solve(void *args)
{
    int id = *((int *)args);
    int board[N];
    int spaces[N];

    while (!shutdown)
    {
        int cur_id = -1;
        if ((cur_id = receive_job()) == -1)
            continue;
        // printf("thread %d get job %d, all %d\n", id, cur_id, total);
        input(puzzle[cur_id], board, spaces);

        if (solve(0, board))
        {
            // 临界区
            pthread_mutex_lock(&inc_mutex);
            ++total_solved;
            pthread_mutex_unlock(&inc_mutex);
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
    }
}

int main(int argc, char *argv[])
{
    char file_path[128];

    // 读取文件名
    char *_ = fgets(file_path, sizeof file_path, stdin);
    if (file_path[strlen(file_path) - 1] == '\n')
        file_path[strlen(file_path) - 1] = '\0';

    if (argc == 2)
        num_t = atoi(argv[1]); // 参数指定线程个数
    printf("threads number is %d\n", num_t);

    int64_t start = now(); // 计时

    FILE *fp = fopen(file_path, "r"); // 打开文件

    for (int i = 0; i < num_t; i++)
    {
        thread_data[i] = i;
        pthread_create(&tt[i], NULL, sudoku_solve, (void *)&thread_data[i]);
    }

    while (fgets(puzzle[total], sizeof puzzle, fp) != NULL)
    {
        if (strlen(puzzle[total]) >= N)
        {
            pthread_mutex_lock(&pnc_mutex);
            total++;
            pthread_mutex_unlock(&pnc_mutex);
        }
    }

    tot_t = total;

    for (int i = 0; i < num_t; i++)
        pthread_join(tt[i], NULL);

    for (int i = 0; i < total; i++)
        printf("%s\n", ansBoard[i]);

    int64_t end = now();
    double sec = (end - start) / 1000000.0;
    printf("%f sec %f ms each %d\n", sec, 1000 * sec / total, total_solved); // 输出运行时间

    return 0;
}
