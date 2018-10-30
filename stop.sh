CURR_DIR=$(pwd)
echo "stopping smtproo server..."
PID=$(cat $CURR_DIR/smtproo.pid 2>/dev/null)
rm -f $CURR_DIR/smtproo.pid
if [ "$PID" ]; then
	if ps -p $PID > /dev/null 2>&1; then
		kill $PID
		STOP_CODE=$?
		echo "process ${PID} was killed"
	else
		echo "process ${PID} not running"
		STOP_CODE=4
	fi
else
	echo "smtproo.pid not found"
	STOP_CODE=4
fi

exit ${STOP_CODE}