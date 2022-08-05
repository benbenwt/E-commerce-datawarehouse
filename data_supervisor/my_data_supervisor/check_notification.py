#! /usr/bin/python3
# -*- coding: utf-8 -*-
import datetime
import smtplib
import sys
from email.header import Header
from email.mime.text import MIMEText

import pymysql

def get_yesterday():
    today=datetime.date.today()
    one_day=datetime.timedelta(days=1)
    yesterday=today-one_day
    return str(yesterday)

def read_table(table,dt):
    con=pymysql.connect(host="172.18.65.187",port=3306,user="root",passwd="root",db="data_supervisor",charset = 'utf8')
    cur=con.cursor()
    query="desc "+table
    cur.execute(query)
    head=map(lambda x:str(x[0]),cur.fetchall())
    query = ("select * from " + table + " where dt='" + dt + "' and `value` not between value_min and value_max")
    print("query:\n",query)
    cur.execute(query)
    cursor_fetchall = cur.fetchall()
    print("query result:\n",cursor_fetchall)
    fetchall = map(lambda x: dict(x), map(lambda x: zip(head, x), cursor_fetchall))
    return fetchall

def main(argv):
    """
    :param argv: 系统参数，共三个，第一个为python脚本本身，第二个为告警方式，第三个为日期
    """
    # 如果没有传入日期参数，将日期定为昨天
    if len(argv) >= 2:
        dt = argv[1]
    else:
        dt = get_yesterday()

    notification_level = 0
    print("dt : ",dt)
    # 遍历所有表，查询所有错误内容，如果大于设定警告等级，就发送警告
    for table in ["day_on_day", "duplicate", "null_id", "rng", "week_on_week"]:
        print(table)
        for line in read_table(table, dt):
            print(line.values())
            if line and line.get('notification_level') >= notification_level:
                line["norm"]=table
                mail_alert(line)

def mail_alert(line):
    """
    使用电子邮件的方式发送告警信息
    :param line: 一个等待通知的异常记录，{'notification_level': 1, 'value_min': 0, 'value': 7, 'col': u'id', 'tbl': u'dim_user_info', 'dt': datetime.date(2021, 7, 16), 'value_max': 5}
    """

    # smtp协议发送邮件的必要设置
    mail_host = "smtp.126.com"
    mail_user = "wt18352516030@126.com"
    mail_pass = "QRQXBTLOJFFDYPDJ"

    # 告警内容
    message = ["".join(["表格", str(line["tbl"]), "数据异常."]),
               "".join(["指标", str(line["norm"]), "值为", str(line["value"]),
                        ", 应为", str(line["value_min"]), "-", str(line["value_max"]),
                        ", 参考信息：" + str(line["col"]) if line.get("col") else ""])]
    # 告警邮件，发件人
    sender = mail_user

    # 告警邮件，收件人
    receivers = [mail_user]

    # 将邮件内容转为html格式
    mail_content = MIMEText("".join(["<html>", "<br>".join(message), "</html>"]), "html", "utf-8")
    mail_content["from"] = sender
    mail_content["to"] = receivers[0]
    mail_content["Subject"] = Header(message[0], "utf-8")

    # 使用smtplib发送邮件
    # try:
    smtp = smtplib.SMTP_SSL(mail_host)
    smtp.connect(mail_host, port=465)
    smtp.login(mail_user, mail_pass)
    content_as_string = mail_content.as_string()
    smtp.sendmail(sender, receivers, content_as_string)
    # except smtplib.SMTPException as e:
    #     print("smtplib.SMTPException")
    #     print(e)

if __name__=="__main__":
    main(sys.argv)