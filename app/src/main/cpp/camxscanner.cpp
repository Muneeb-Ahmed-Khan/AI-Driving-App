//
// Created by muneeb on 09/02/2024.
//

#include "camxscanner.h"
#include <string>
#include <chrono>
#include <math.h>
#include <vector>
#include <opencv2/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/dnn/layer.details.hpp>
#include <iostream>
#include <typeinfo>

#ifdef __ANDROID__
#include <android/log.h>
#define TAG "NativeLib"
#endif

using namespace cv;
using namespace std;

cv::dnn::Net model;

const int INPUT_WIDTH = 640;
const int INPUT_HEIGHT = 640;
const float SCORE_THRESHOLD = 0.5;
const float NMS_THRESHOLD = 0.3;

// Text parameters.
const float FONT_SCALE = 0.7;
const int FONT_FACE = cv::FONT_HERSHEY_SIMPLEX;
const int THICKNESS = 1;

// Colors.
Scalar BLACK = Scalar(0, 0, 0);
Scalar BLUE = Scalar(255, 178, 50);
Scalar YELLOW = Scalar(0, 255, 255);
Scalar RED = Scalar(0, 0, 255);


/**
 * The function takes path to model and load it.
 *
 * @param modelPath A std::string having a path where model file is located.
 *
 * @return The function returns True if model is loaded. False if model could not be loaded.
 */
bool loadOnnxModel(std::string modelPath){
    model = cv::dnn::readNetFromONNX(modelPath);

    if (model.empty()) {
        std::cerr << "Failed to load the ONNX model." << std::endl;
        return false;
    }

    return true;
}