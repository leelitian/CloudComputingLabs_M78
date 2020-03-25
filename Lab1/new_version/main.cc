#include <assert.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <sys/time.h>
#include <stdlib.h>
#include <sys/sysinfo.h>
#include <pthread.h>

#include "sudoku.h"

char puzzle[PNUM][83];
char result[PNUM][83];

pthread_mutex_t jobQueueMutex = PTHREAD_MUTEX_INITIALIZER;
int thread_number = get_nprocs_conf(); // 获取CPU核心数作为线程数
FILE *fp = NULL;

int total_solved = 0;

int64_t now()
{
    struct timeval tv;
    gettimeofday(&tv, NULL); // 获取当前精确时间
    return tv.tv_sec * 1000000 + tv.tv_usec;
}

struct Job
{
    int begin, end;
    Job(int a, int b) : begin(a), end(b) {}
};

Job recvAJob()
{
    pthread_mutex_lock(&jobQueueMutex);
    int begin = total_solved;
    size_t num = fread(puzzle[total_solved], 83, BATCH, fp);
    // for (int i = 0; i < BATCH; ++i)
    // {
    //     if (fgets(puzzle[job.end], 128, fp) == NULL)
    //         break;
    //     ++job.end;
    // }

    if (num == 0)
    {
        pthread_mutex_unlock(&jobQueueMutex);
        return Job(-1, -1);
    }

    total_solved += num;
    pthread_mutex_unlock(&jobQueueMutex);
    return Job(begin, begin + num);
}

void proceeAjob(int begin, int end)
{
    // printf("[thread %ld],\t %d-%d\n", pthread_self(), begin, end);
    int spaces[N];
    int board[N];
    for (int i = begin; i < end; ++i)
    {
        input(puzzle[i], board, spaces);
        solve_sudoku_dancing_links(board);
        if (!solved(board))
            assert(0);
        for (int j = 0; j < 81; ++j)
            result[i][j] = board[j] + '0';
        result[i][81] = '\0';
    }
}

void *thread_work(void *args)
{
    int *a = (int *)args;
    cpu_set_t cpuset;
    
    pthread_t thread = pthread_self();

    CPU_ZERO(&cpuset);
    CPU_SET(*a, &cpuset);

    // 设置线程CPU亲和力
    pthread_setaffinity_np(thread, sizeof(cpu_set_t), &cpuset);

    Job job(0, 0);
    while (1)
    {
        job = recvAJob();
        if (job.begin == -1)
            return NULL;
        proceeAjob(job.begin, job.end);
    }
}

int main(int argc, char *argv[])
{
    char file_path[128];
    char puzzle[128]; //输入的谜题

    if (argc == 2)
        thread_number = atoi(argv[1]); // 参数指定线程个数

    // 读取文件名
    char *_ = fgets(file_path, sizeof file_path, stdin);
    if (file_path[strlen(file_path) - 1] == '\n')
        file_path[strlen(file_path) - 1] = '\0';

    fp = fopen(file_path, "r"); // 打开文件

    int64_t start = now(); // 计时

    // 创建线程
    pthread_t th[thread_number];
    int thid[thread_number];
    printf("threads number is %d\n", thread_number);
    for (int i = 0; i < thread_number; i++)
    {
        thid[i] = i % get_nprocs_conf();
        if (pthread_create(&th[i], NULL, thread_work, (void *)&thid[i]) != 0)
        {
            perror("pthread_create failed");
            exit(1);
        }
    }

    // 主线程等待
    for (int i = 0; i < thread_number; i++)
        pthread_join(th[i], NULL);

    for (int i = 0; i < total_solved; ++i)
    {
        printf("%s\n", result[i]);
    }

    int64_t end = now();
    double sec = (end - start) / 1000000.0;
    printf("%f sec %f ms each %d\n", sec, 1000 * sec / total_solved, total_solved); // 输出运行时间

    return 0;
}
