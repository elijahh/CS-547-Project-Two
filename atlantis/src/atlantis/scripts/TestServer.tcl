
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

    unset Server(state)

    if [ catch { close $sock } errMsg ] {
        
        puts "terminate error: $sock: $errMsg"
    }
}

proc Transmit_Map_Attributes { sock } {

    global SERVER_MAP_NAME

    puts $sock $SERVER_MAP_NAME

    Terminate $sock
}

proc Process { sock } {

    global Server

    switch $Server(state) {

        0 { Transmit_Map_Attributes $sock }
    }
}

proc Accept { sock addr port } {

    global Server

    if [ info exists Server(state) ] {

        puts "rejected: $sock: connection in progress"
        catch { close $sock }
    }

    set Server(state) 0

    fconfigure $sock -buffering line
    fileevent $sock readable [ list Process $sock ]

    puts "accepted: $sock $addr $port"
}

socket -server Accept 6000
vwait forever

