import json
import os


input_dir= '00_pos_baseline'
output_dir= 'output'
if not os.path.exists(output_dir):
    os.makedirs;
for filename in os.listdir(input_dir):
    if filename.endswith('.json'):
        with open(os.path.join(input_dir,filename),'r') as file:
            data= json.load(file)

        #total_objects = len(data)
        #num_digits = len(str(total_objects))
        for index, obj in enumerate(data, start=1):
            #index = index +1
            obj['US_Nr'] = f'user_story_{index:02d}'

            
        output_filename=os.path.join(output_dir,filename)
        with open(output_filename,'w') as file:
            json.dump(data, file,indent=2)
