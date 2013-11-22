
#
# TestServer.tcl
#
# HT: ActiveTcl 8.6.0.0 Help file for information about setting up a simple
#     server process in Tcl. 
#
#     Additional resources included Ousterhout's "Tcl and the Tk Toolkit" 
#     and Welch's "Practical Programming in Tcl and Tk".
#

# -----------------------------------------------------------------------------

#
# Constants
#

set SERVER_MAP_NAME "mapname"

# -----------------------------------------------------------------------------

proc Terminate { sock } {

    global Server

    unset Server($sock,state)

    if [ catch { close $sock } errMsg ] {
        
        puts "terminate error: $sock: $errMsg"
    }
}

proc Transmit_Position_Updates { sock } {

    set rc [ catch { gets $sock buffer } errMsg ]

    if { 0 != $rc } {

        puts "error reading socket: $sock: $errMsg"
        Terminate $sock

    } else {

        if [ eof $sock ] {

            puts "connection ended: $sock"
            Terminate $sock
        }
    }
}

proc Transmit_Map_Attributes { sock } {

    global Server

    global SERVER_MAP_NAME

    puts $sock $SERVER_MAP_NAME

    set Server($sock,state) 1
}

proc Process { sock } {

    global Server

    switch $Server($sock,state) {

        0 { Transmit_Map_Attributes $sock }
        1 { Transmit_Position_Updates $sock }
    }
}

proc Accept { sock addr port } {

    global Server

    set Server($sock,state) 0

    fconfigure $sock -buffering line
    fileevent $sock readable [ list Process $sock ]

    puts "accepted: $sock $addr $port"
}

socket -server Accept 6000
vwait forever

