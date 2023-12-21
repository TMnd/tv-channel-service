#! /bin/sh

currentTime=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
timeOfStart=0
randomShowEndPoint="http://10.10.0.222:8080/api/tv/nextShow?time=$currentTime"
httpStatusCode=""

# show structure
name="" #: "Angry Video Game Nerd: The Movie"
path="" # "/mnt/disk1/Videos/Filmes/Angry Video Game Nerd The Movie.mp4",
duration="" #: 6890,
episode="" # null
season="" # null,
seriesName="" # null


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

    
    case $day_of_week in
        1|2|3|4|5)
            timeOfStart=16
            ;;
        *)
            timeOfStart=8
            ;;
    esac

}

get_random_show() {
    json_response=$(curl -s -w "\n%{http_code}" $randomShowEndPoint)
    echo $json_response
    httpStatusCode=$(echo "$json_response" | tail -n 1)
    name=$(echo "$json_response" | jq -r '.name')
    path=$(echo "$json_response" | jq -r '.path')
    duration=$(echo "$json_response" | jq -r '.duration')
    episode=$(echo "$json_response" | jq -r '.episode')
    season=$(echo "$json_response" | jq -r '.season')
    seriesName=$(echo "$json_response" | jq -r '.seriesName')
}

# Function to transition to the next state
transition_state() {
    case $1 in
        $STATE_CHECK_TIME)
            echo "Current time: " $(get_current_hour) H
            set_time_of_start
            echo "Today's starting time: " $timeOfStart H
            if [ $(get_current_hour) -ge 16 ]; then
                echo "get video info"
                current_state=$STATE_GET_VIDEO
            else
                echo "check video"
                current_state=$STATE_OFFLINE
            fi
            ;;
        $STATE_GET_VIDEO)
            get_random_show
            if [ $httpStatusCode -eq 200 ]; then
                echo "Show intermission"
                echo $name
                echo $path
                echo $duration
                echo $episode
                echo $season
                echo $seriesName
                current_state=$STATE_INTERMISSION
            else
                echo "go to offline state"
                current_state=$STATE_OFFLINE
            fi
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