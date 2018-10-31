CURR_DIR=$(pwd)
touch $CURR_DIR/smtproo.log
echo "starting smtproo server ..."
java -Dline.separator=$'\r\n' -jar smtproo.jar >> $CURR_DIR/smtproo.log &
echo $! > $CURR_DIR/smtproo.pid
PID=$(cat $CURR_DIR/smtproo.pid 2>/dev/null)
echo "started smtproo server... process id : $PID"

