#!/bin/sh

app_base="/opt/AcademicNT/mce"
app_name="acadnt_mce"

case "$1" in
    start)
        log_file="${app_base}/${app_name}.log"
        pid_file="${app_base}/${app_name}.pid"

        if [ -r "$pid_file" ]; then
            pid=`cat $pid_file`
            app_run=`ps -efo pid | grep -v grep | grep $pid`
        else
            app_run=""
        fi

        if [ -n "$app_run" ]; then
            echo "${app_name} is loaded"
        else
            echo "Loading ${app_name}... \c"
            touch $log_file
            echo "["`date '+%d/%m/%Y %H:%M:%S'`"] Loading ${app_name} server..." >> $log_file
            cd $app_base && nohup $JAVA_HOME/bin/java $JAVA_OPTS -jar server.jar >> $log_file 2>&1& echo $! > $pid_file
            echo "done"
        fi
    ;;
    stop)
        log_file="${app_base}/${app_name}.log"
        pid_file="${app_base}/${app_name}.pid"
        if [ ! -r "$pid_file" ]; then
            echo "${app_name} not loaded"
        else
            echo "Unloading ${app_name}... \c"
            if [ -f $pid_file ]; then
                echo "["`date '+%d/%m/%Y %H:%M:%S'`"] Unloading ${app_name} server..." >> $log_file
                kill -TERM `cat $pid_file`
                rm $pid_file
            fi
            echo "done"
        fi
    ;;
    *)
        echo "$0 {start|stop}"
        exit 1
    ;;
esac
