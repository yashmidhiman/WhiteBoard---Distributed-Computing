
A Shared White Board JAVA Application

It facilitates a number of users to share white board with others and 
free to draw patterns togetherly and choose different colors & eraser.
Sharing is done over multicast address and port number.

It has .jar/.exe file and the source file. Please remove extension .txt (eg. WhiteBoard.jar.txt => WhiteBoard.jar)
Launch .jar/.exe or compile and run the Java program.
Inputs: <multicast-address>
		<port-number>
click: 	connect
Draw patterns.

If the user has made a connection and doing actions (drawing, erasing,..).
action information is wrapped into a packet and sends it to the connected multicast group, 
packets received by the group (using Threads) call for appropriate functions for drawing. 