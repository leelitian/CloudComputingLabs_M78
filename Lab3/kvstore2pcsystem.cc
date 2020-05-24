#include "stdlib.h"
#include <unistd.h>
#include <getopt.h>
#include <iostream>
#include <cstring>
using namespace std;

int SendSms(const char *cmd)
{
    int ret = 0;
    ret = system(cmd);
    if (ret != 0)
    {
        return -1;
    }

    return 0;
}

int main(int argc, char *argv[])
{
    int opt;

    static struct option long_options[] =
        {
            {"config_path", required_argument, NULL, 'c'},
        };

    string config_path;
    while ((opt = getopt_long_only(argc, argv, "a:", long_options, NULL)) != -1)
    {
        switch (opt)
        {
        case 'c':
            config_path = optarg;
            break;
        default:
            break;
        }
    }

    string strSendSms = "./kvstore2pcsystem.sh " + config_path;

    return SendSms(strSendSms.c_str());
}
