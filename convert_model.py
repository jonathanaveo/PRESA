import pickle
import json
import numpy as np
import base64

def convert_model():
    try:
        # Load the XGBoost model
        print("Loading model...")
        with open('/Users/jonathanaveo/Downloads/model.pkl', 'rb') as f:
            model_bytes = f.read()
        
        # Convert model to base64
        model_base64 = base64.b64encode(model_bytes).decode('utf-8')
        
        # Create model info
        model_info = {
            'model_base64': model_base64,
            'feature_names': ['temperature', 'rainfall', 'fertilizer', 'irrigation', 'days'],
            'input_shape': 5
        }
        
        # Save as JSON
        with open('app/src/main/assets/model_info.json', 'w') as f:
            json.dump(model_info, f)
            
        print("Model converted successfully!")
        
    except Exception as e:
        print("Error:", str(e))
        print("Error type:", type(e).__name__)

if __name__ == '__main__':
    convert_model()
