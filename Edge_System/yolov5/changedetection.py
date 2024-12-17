import os
import cv2
import pathlib
import requests
from datetime import datetime

class ChangeDetection:
    
    HOST = 'https://pinkanimal.pythonanywhere.com'
    username = ''
    password = ''
    token = '3f8b7598a118c99b5a33e658164b2678980a3ada'
    title = '출석을 확인해 주세요'
    text = '출석을 확인해 주세요'
    personIndex = 0
    flag = 0

    def __init__(self, names):
        for i in range(len(names)):
            if names[i] == 'person':
                self.personIndex = i
        

        
        print(self.token)

    def add(self, conf, detected_current, save_dir, image):
        self.title = ''
       
        self.text = ''

        
      
        
        
        if detected_current[self.personIndex]==1:
            if conf[self.personIndex]>0.95:
        
                if self.flag == 0:
                    self.send(save_dir, image)
                    self.flag = 1
           
        else:
            self.flag=0
        
        
        
    def send(self, save_dir, image):
        now = datetime.now()
        now.isoformat()
        today = datetime.now()
        save_path = os.getcwd() / save_dir / 'detected' / str(today.year) / str(today.month) / str(today.day) 
        pathlib.Path(save_path).mkdir(parents=True, exist_ok=True)
        full_path = save_path / '{0}-{1}-{2}-{3}.jpg'.format(today.hour,today.minute,today.second,today.microsecond)
        dst = cv2.resize(image, dsize=(320, 240), interpolation=cv2.INTER_AREA)
        cv2.imwrite(full_path, dst)

        headers = {'Authorization' : 'JWT ' + self.token, 'Accept' : 'application/json'}
        formatted_date = now.strftime('%Y-%m-%d %H:%M:%S')
        data = {
            'author' : 1,
            'title' : formatted_date, 
            'text' : '출석을 확인해 주세요', 
            'created_date' : now, 
            'published_date' : now,
            'checked': False,
        }
        file = {'image' : open(full_path, 'rb')}
        res = requests.post(self.HOST + '/api_root/Post/', data=data, files=file, headers=headers)
        print(res)
