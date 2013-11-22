
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

    if { 0 != $rc } {

        error "error reading socket: $sock: $errMsg"
        catch { close $sock }

    } else {

        if [ eof $sock ] { close $sock; exit 0 }

        puts $buffer

        puts $sock "OK"
    }
}

set rc [ catch { set sock [ socket "localhost" 6000 ] } errMsg ]

if { 0 != $rc } {
 
    error "could not establish connection: $errMsg"
}

fconfigure $sock -buffering line
fileevent $sock readable [ list Process $sock ]

puts $sock "HELLO"

vwait forever

