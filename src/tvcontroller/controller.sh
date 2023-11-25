#! /bin/sh

serviceUrl=localhost
servicePort=8080
timeOfStart=0

STATE_GET_VIDEO="get"
STATE_INTERMISSION="intermission"
STATE_SHOW="show"
STATE_CHECK_TIME="checkTime"
STATE_OFFLINE="offline"

# Initial state
current_state=$STATE_CHECK_TIME

# Function to show the file
start_video() {
    # Path to the video file
    video_file="./sample.mp4"

    # Start VLC in the background with the video file
    vlc --play-and-exit  "$video_file" &

    # Capture the process ID (PID) of the VLC process
    vlc_pid=$!

    # Function to check if the VLC process is still running
    is_vlc_running() {
        if ps -p $vlc_pid > /dev/null; then
            return 0  # VLC is running
        else
            return 1  # VLC is not running
        fi
    }

    # Wait for the VLC process to finish (video playback to complete)
    while is_vlc_running; do
        sleep 1
    done

    # Kill the VLC process once the video stops
    kill $vlc_pid

    echo "Video playback finished. VLC process terminated."
}

get_current_hour() {
    date +"%H"
}

set_time_of_start() {
    day_of_week=$(date +%u)
    
    echo "0"
    
    case $day_of_week in
        1|2|3|4|5)
            timeOfStart=16
            ;;
        *)
            timeOfStart=8
            ;;
    esac

}

# Function to transition to the next state
transition_state() {
    case $1 in
        $STATE_CHECK_TIME)
            echo "Current time: " $(get_current_hour)
            set_time_of_start
            echo "Today's starting time: " $timeOfStart
            if [ $(get_current_hour) -ge 16 ]; then
                echo "get video info"
                current_state=$STATE_GET_VIDEO
            else
                echo "check video"
                current_state=$STATE_OFFLINE
            fi
            ;;
        $STATE_GET_VIDEO)
            echo "Show intermission"
            current_state=$STATE_INTERMISSION
            ;;
        $STATE_INTERMISSION)
            echo "Show episode/movie"
            current_state=$STATE_SHOW
            ;;
        $STATE_SHOW)
            echo "Star all over again"
            current_state=$STATE_CHECK_TIME
            ;;
        $STATE_OFFLINE)
            echo "Retry...."
            current_state=$STATE_CHECK_TIME
            ;;
        *)
            echo "Invalid state"
            ;;
    esac
}

main() {
    while true; do
        case $current_state in
            $STATE_CHECK_TIME)
                echo "Check date"
                sleep 3
                transition_state $current_state
                ;;
            $STATE_GET_VIDEO)
                echo "Get next show data"
                sleep 3
                transition_state $current_state
                ;;
            $STATE_INTERMISSION)
                echo "Show intermission data"
                sleep 2
                transition_state $current_state
                ;;
            $STATE_SHOW)
                echo "show show"
                start_video
                transition_state $current_state
                ;;
            $STATE_OFFLINE)
                echo "Offline mode"
                sleep 60
                transition_state $current_state
                ;;
            *)
                echo "Invalid state"
                exit 1
                ;;
        esac
    done
}

echo "Script started:" $(date)
main