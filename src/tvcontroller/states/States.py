import subprocess
import psutil
import time, requests, json, vlc
from .MainState import State
from datetime import datetime


def is_vlc_running():
    for process in psutil.process_iter(['pid', 'name']):
        if 'vlc' in process.info['name'].lower():
            return True
    return False

def runVlc(media, showdata, showtime):

    # Create VLC instance
    instance = vlc.Instance('--no-xlib')

    # Create media player
    media_player = instance.media_player_new()

    # Set marquee text with line break
    media_player.video_set_marquee_int(vlc.VideoMarqueeOption.Enable, 1)
    media_player.video_set_marquee_string(vlc.VideoMarqueeOption.Text, showdata+"\n\r\n"+showtime)
    media_player.video_set_marquee_int(vlc.VideoMarqueeOption.Y, 550)
    media_player.video_set_marquee_int(vlc.VideoMarqueeOption.X, 100)

    # Load media
    media = instance.media_new(media)
    media.get_mrl()
    media_player.set_media(media)

    # Play the media
    media_player.play()

    # Wait for the media to finish
    time.sleep(30)  # Adjust as needed

    # Release the media player
    media_player.release()


class StateCheckTime(State):
    """
    Check if is late night, between midnight and 2 am
    """
    def isLateTime(self, current_hour: int):
        return 0 <= current_hour <= 2

    """
    Check if is time to show anything
    """
    def isTimeToShow(self, current_hour: int):
        current_datetime = datetime.now()
        current_day_of_week = current_datetime.weekday()

        match current_day_of_week:
            case 0, 1, 2, 3, 4:
                print(" -- It's week day")
                if current_hour >= 16 or StateCheckTime.isLateTime(self, current_hour):
                    return True
            case _:
                print(" -- It's weekend")
                if current_hour >= 8 or StateCheckTime.isLateTime(self, current_hour):
                    return True
                return False

    """
    Check if the current time is correct to show any episodes/moveis based of the week day
    """
    def on_event(self, event):
        print(" - Current state: STATE_CHECK_TIME")
        time.sleep(3)
        currentHour = datetime.now().hour
        if self.isTimeToShow(currentHour):
            StateGetVideo.on_event(self, None)
        else:
            StateOffline.on_event(self, None)
        return self


class StateGetVideo(State):
    """
    Get current time in UTC
    """
    def getCurrentTimeUtc(self):
        current_time = datetime.utcnow()
        formatted_time = current_time.strftime('%Y-%m-%dT%H:%M:%SZ')
        return formatted_time

    """
    Make a http request to get the next episode/video to show
    """
    def on_event(self, event):
        print(" - Current state: STATE_GET_VIDEO")
        time.sleep(2)
        try:
            currentTime = StateGetVideo.getCurrentTimeUtc(self)
            url = "http://localhost:8080/api/tv/nextShow?time=" + str(currentTime)

            url = "http://localhost:8080/api/tv/nextShow?time=2023-12-19T20:54:40Z"

            response = requests.get(url)

            print(response)

            if response.status_code == 200:
                StateIntermission.on_event(self, response.text)
            else:
                StateOffline.on_event(self, response)

        except Exception as e:
            print(f"An error occurred: {e}")
            StateOffline.on_event(self, None)

        return self


class StateIntermission(State):
    """
    Show intermission beetwen shows
    """

    def on_event(self, event):
        print(" - Current state: STATE_INTERMISSION")
        data = json.loads(event)
        videoPath = data["path"]
        seriesName = data["seriesName"]
        duration = data["duration"]
        episode = data["episode"]
        season = data["season"]

        showinfo = "Coming up next, we have.. "

        if episode!=None and season!=None:
            showinfo += seriesName + "Season: " + season + " - Episode: " + episode
        else:
            showinfo += seriesName

        showtime = "It is scheduled to finish by" + duration;

        runVlc("intermission.mp4", showinfo, showtime)

        time.sleep(1)

        if is_vlc_running:
            StateShow.on_event(self, videoPath)
        else:
            StateOffline.on_event(self, None)

        return self


class StateShow(State):
    """
    Show the requestes show/movie
    """

    def on_event(self, video_path):
        print(" - Current state: STATE_SHOW")

        print(video_path)

        command = ["vlc", "--play-and-exit", video_path]

        # Use subprocess to execute the command in the command line
        subprocess.run(command)

        time.sleep(2)

        if is_vlc_running:
            StateGetVideo.on_event(self, None)
        else:
            StateOffline.on_event(self, None)

        return self


class StateOffline(State):
    """
    Idle state.
    """

    def on_event(self, event):
        print(" - Current state: STATE_OFFLINE")
        print(" -- In idle... waiting 60 seconds")
        time.sleep(60)
        StateCheckTime.on_event(self, None)
        return self
