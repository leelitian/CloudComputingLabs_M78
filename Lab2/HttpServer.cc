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
            {"ip", required_argument, NULL, 'i'},
            {"port", required_argument, NULL, 'p'},
            {"number-thread", required_argument, NULL, 'n'},
            {NULL, 0, NULL, 0},
        };

    string tip, tport, tnumber_thread;
    while ((opt = getopt_long_only(argc, argv, "a:b:c:d", long_options, NULL)) != -1)
    {
        switch (opt)
        {
        case 'i':
            tip = optarg;
            break;
        case 'p':
            tport = optarg;
            break;
        case 'n': // ip’s short char
            tnumber_thread = optarg;
            break;
        default:
            break;
        }
    }

    string strSendSms = "./HttpServer.sh " + tport + " " + tnumber_thread;
    //        const char * ip = "******";
    //        int port = 8888;
    //        const char * account = "******";
    //        const char * password = "******";
    //        const char * phone = "******";
    //        const char * content = "******";
    //	strSendSms.nFormat(1024, "sh %s \'%s\' %d \'%s\' \'%s\' \'%s\' \'%s\'",
    //					   (LPCTSTR) SEND_MESSAGE,
    //					   (LPCTSTR) ip,
    //					    port,
    //					   (LPCTSTR) account,
    //					   (LPCTSTR) password,
    //					   (LPCTSTR) phone,
    //					   (LPCTSTR) content);

    return SendSms(strSendSms.c_str());
}
