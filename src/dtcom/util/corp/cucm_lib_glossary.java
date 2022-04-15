package dtcom.util.corp;
import javax.telephony.callcontrol.events.*;
import javax.telephony.events.*;
import com.cisco.jtapi.extensions.CiscoAddress;
import com.cisco.jtapi.extensions.CiscoTerminal;

public class cucm_lib_glossary {
	
    //library for cisco address state
    public String lib_csc_addr_state(int ID){
        String state = "";
        switch(ID){
            case CiscoAddress.OUT_OF_SERVICE :  state = "Address is out of service";break;
            case CiscoAddress.IN_SERVICE : state = "Address is in service";break;
        }
        return state;
    }

    //library for cisco terminal state
    public String lib_csc_term_state(int ID){
        String state = "";
        switch(ID){
            case CiscoTerminal.OUT_OF_SERVICE : state = "Terminal is out of service";break;
            case CiscoTerminal.IN_SERVICE : state = "Terminal is in service";break;
        }
        return state;
    }

    //library for cisco terminal device state
    public String lib_csc_term_dev_state(int ID){
        String state = "";
        switch(ID){
            case CiscoTerminal.DEVICESTATE_IDLE : state = "Device state is idle";break; 
            case CiscoTerminal.DEVICESTATE_ALERTING : state = "Device state is alerting";break;
            case CiscoTerminal.DEVICESTATE_ACTIVE : state = "Device state is active";break;
            case CiscoTerminal.DEVICESTATE_HELD : state = "Device state is held";break;
            case CiscoTerminal.DEVICESTATE_UNKNOWN : state = "Device state is unknown";break;
        }
        return state;
    }

    //not yet clustered
    //library for JTAPI's standart event state
    public String lib_event(int ID){
        String state = "";
        switch(ID){
            // provider events
            case ProvInServiceEv.ID:			  state = "Provider is in service";break;
            case ProvOutOfServiceEv.ID:			  state = "Provider is out of service";break;
            case ProvObservationEndedEv.ID:		  state = "Provider Observer has been ended";break;
            case ProvShutdownEv.ID:                       state = "Provider has been shutdown";break;

            // call events
            case CallActiveEv.ID:                 state = "Call state is active";break;
            case CallInvalidEv.ID:                state = "Call state is invalid";break;
            case CallObservationEndedEv.ID:       state = "Call Observer has been ended";break;

            // connection events
            case ConnCreatedEv.ID:                state = "Connection state is created";break;
            case ConnInProgressEv.ID:             state = "Connection state is in progress";break;
            case ConnAlertingEv.ID:               state = "Connection state is alert";break;
            case ConnConnectedEv.ID:              state = "Connection state is connected";break;
            case ConnDisconnectedEv.ID:           state = "Connection state is disconnected";break;
            case ConnFailedEv.ID:                 state = "Connection state is failed";break;
            case ConnUnknownEv.ID:                state = "Connection state is unknown";break;

            // terminal connection events
            case TermConnCreatedEv.ID:            state = "Terminal Connection state is created";break;
            case TermConnRingingEv.ID:            state = "Terminal Connection state is ringing";break;
            case TermConnActiveEv.ID:             state = "Terminal Connection state is active";break;
            case TermConnPassiveEv.ID:            state = "Terminal Connection state is passive";break;
            case TermConnDroppedEv.ID:            state = "Terminal Connection state is dropped";break;
            case TermConnUnknownEv.ID:            state = "Terminal Connection state is unknown";break;

            // call control connection events
            case CallCtlConnAlertingEv.ID:        state = "CCConn Alerting";break;
            case CallCtlConnDialingEv.ID:         state = "CCConn Dialing";break;
            case CallCtlConnDisconnectedEv.ID:    state = "CCConn Disconnected";break;
            case CallCtlConnEstablishedEv.ID:     state = "CCConn Established";break;
            case CallCtlConnFailedEv.ID:          state = "CCConn Failed";break;
            case CallCtlConnInitiatedEv.ID:       state = "CCConn Initiated";break;
            case CallCtlConnNetworkAlertingEv.ID: state = "CCConn NetworkAlerting";break;
            case CallCtlConnNetworkReachedEv.ID:  state = "CCConn NetworkReached";
            case CallCtlConnOfferedEv.ID:         state = "CCConn Offered";break;
            case CallCtlConnQueuedEv.ID:          state = "CCConn Queued";break;
            case CallCtlConnUnknownEv.ID:         state = "CCConn Unknown";break;

            // call control terminal connection events
            case CallCtlTermConnBridgedEv.ID:     state = "CCTermConn Bridged";break;
            case CallCtlTermConnDroppedEv.ID:     state = "CCTermConn Dropped";break;
            case CallCtlTermConnHeldEv.ID:        state = "CCTermConn Held";break;
            case CallCtlTermConnInUseEv.ID:       state = "CCTermConn InUse";break;
            case CallCtlTermConnRingingEv.ID:     state = "CCTermConn Ringing";break;
            case CallCtlTermConnTalkingEv.ID:     state = "CCTermConn Talking";break;
            case CallCtlTermConnUnknownEv.ID:     state = "CCTermConn Unknown";break;
        }
        return state;
    }

}

