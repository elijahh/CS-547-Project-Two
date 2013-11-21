
#
# TestClient.tcl
#
# HT: ActiveTcl 8.6.0.0 Help file for information about setting up a simple
#     client process in Tcl. 
#
#     Additional resources included Ousterhout's "Tcl and the Tk Toolkit" 
#     and Welch's "Practical Programming in Tcl and Tk".
#

proc Process { sock } {

    set rc [ catch { gets $sock buffer } errMsg ]

    if { 0 != rc } {

        catch { close $sock }
        error "error reading socket: $sock: $errMsg"

    } else {

        puts $buffer
    }
}

set rc [ catch { set sock [ socket "localhost" 6000 ] } errMsg ]

if { 0 != $rc } {
 
    error "could not establish connection: $errMsg"
}

fconfigure $sock -buffering line
fileevent $sock readable [ list Process $sock ]

vwait forever

