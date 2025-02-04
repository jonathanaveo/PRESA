import os
import json
import pickle
import base64
import numpy as np
import xgboost as xgb
from android.os import Environment

def get_model_info_path():
    # Get the app's assets directory
    assets_dir = os.path.join(os.path.dirname(__file__), "..", "..", "assets")
    return os.path.join(assets_dir, "model_info.json")

def predict(input_data):
    try:
        # Load model info
        with open(get_model_info_path(), 'r') as f:
            model_info = json.load(f)
        
        # Decode model from base64
        model_bytes = base64.b64decode(model_info['model_base64'])
        model = pickle.loads(model_bytes)
        
        # Convert input to numpy array and reshape for prediction
        X = np.array(input_data, dtype=np.float32).reshape(1, -1)
        
        # Convert to DMatrix for XGBoost prediction
        dtest = xgb.DMatrix(X)
        
        # Make prediction
        prediction = model.predict(dtest)
        
        # Return the first prediction (since we only predict for one sample)
        return float(prediction[0])
    except Exception as e:
        print(f"Error in prediction: {str(e)}")
        return 0.0
