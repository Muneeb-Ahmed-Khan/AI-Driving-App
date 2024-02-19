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
vector<string> class_list = {
        "Give way",
        "Mandatory way",
        "Multiple Chevron Right",
        "No Right Turn",
        "No U Turn ahead",
        "No entry",
        "No goods vehicles",
        "No turning for lorries",
        "Pedestrian walking",
        "Right lane closure ahead",
        "Roundabout",
        "Roundabout ahead",
        "Single Chevron Right",
        "Single Chevron left",
        "Skewed side road junction right",
        "U Turn ahead",
        "beginning of median",
        "dead-end",
        "pass either side",
        "right lane open ahead",
        "speed hump",
        "speed limit",
        "stop",
        "trucks not permitted"
};

std::vector<std::string> layerNames = {"output0"};



const int INPUT_WIDTH = 640;
const int INPUT_HEIGHT = 640;
const float SCORE_THRESHOLD = 0.5;
const float NMS_THRESHOLD = 0.3;

// Text parameters.
const float FONT_SCALE = 1;
const int FONT_FACE = cv::FONT_HERSHEY_DUPLEX;
const int THICKNESS = 2;

// Colors.
Scalar BLUE = Scalar(255, 178, 50);
Scalar RED = Scalar(0, 0, 255);


void draw_label(Mat& input_image, string label, int left, int top)
{
    int baseLine;
    Size label_size = getTextSize(label, FONT_FACE, FONT_SCALE, THICKNESS, &baseLine);
    top = max(top, label_size.height);
    Point tlc = Point(left, top);
    Point brc = Point(left + label_size.width, top + label_size.height + baseLine);
    rectangle(input_image, tlc, brc, cv::Scalar(0, 0, 0), FILLED);
    putText(input_image, label, Point(left, top + label_size.height), FONT_FACE, FONT_SCALE, cv::Scalar(0, 255, 0), THICKNESS);
}

void draw_accuracy(Mat& input_image, string label, int left, int top)
{
    int baseLine;
    Size label_size = getTextSize(label, FONT_FACE, FONT_SCALE, THICKNESS, &baseLine);
    top = max(top, label_size.height);

    Point tlc = Point(left, top + label_size.height + baseLine);
    Point brc = Point(left + label_size.width, top + (label_size.height * 2) + baseLine);

    rectangle(input_image, tlc, brc, cv::Scalar(0, 0, 0), FILLED);

    putText(input_image, label, Point(left, top + (label_size.height * 2) + baseLine), FONT_FACE, FONT_SCALE, cv::Scalar(0, 255, 0), THICKNESS);
}


cv::Mat prepare_input(Mat& image) {
    return cv::dnn::blobFromImage(image, 1 / 255.0, cv::Size(INPUT_WIDTH, INPUT_HEIGHT), cv::Scalar(), true, false);
}


std::vector<string> post_process(cv::Mat& input_image, cv::Mat& output0_boxes, const vector<string>& class_name) {

    vector<int> class_ids;
    vector<float> confidences;
    vector<cv::Rect> boxes;
    int INPUT_IMAGE_HEIGHT = input_image.rows;
    int INPUT_IMAGE_WIDTH = input_image.cols;
    std::vector<string> labels;

    float* data = (float*)output0_boxes.data;
    const int rows = output0_boxes.size[0];
    const int dimensions = output0_boxes.size[1];

    for (int i = 0; i < rows; i++) {
        cv::Mat scores(1, class_name.size(), CV_32FC1, data + 4);
        cv::Point class_id;
        double max_class_score;
        cv::minMaxLoc(scores, 0, &max_class_score, 0, &class_id);

        if (max_class_score > SCORE_THRESHOLD)
        {
            confidences.push_back(max_class_score);
            class_ids.push_back(class_id.x);
            float cx = data[0];
            float cy = data[1];
            float w = data[2];
            float h = data[3];
            int left = int(((cx - (w / 2)) / 640) * input_image.cols);
            int top = int(((cy - (h / 2)) / 640) * input_image.rows);
            int width = int((w / 640) * input_image.cols);
            int height = int((h / 640) * input_image.rows);
            boxes.push_back(Rect(left, top, width, height));
        }
        data += dimensions;
    }
    vector<int> indices;
    cv::dnn::NMSBoxes(boxes, confidences, SCORE_THRESHOLD, NMS_THRESHOLD, indices);
    for (int i = 0; i < indices.size(); i++)
    {
        int idx = indices[i];
        Rect box = boxes[idx];

        int left = box.x;
        int top = box.y;
        int width = box.width;
        int height = box.height;
        cv::rectangle(input_image, cv::Point(left, top), Point(left + width, top + height), BLUE, THICKNESS * 3);

        string label = class_name[class_ids[idx]];
        string accuracy = "Accuracy: " + format("%.2f", confidences[idx] * 100) + "%";

        draw_label(input_image, label, left, top);
        draw_accuracy(input_image, accuracy, left, top);

        labels.push_back(class_name[class_ids[idx]]);
    }
    return labels;
}



/**
 * The function process Image and return of 4 corners of document.
 *
 * @param matAddr A long variable that has cv::Mat type data in it.
 *
 * @return The function returns a `std::vector<cv::Point>` which contains the reordered
 * points in following sequence.
 * top-left, top-right, bottom-right, bottom-left.
 */
std::vector<string> processFrame(long matAddr){

    // get Mat from raw address
    Mat &image = *(Mat *) matAddr;
    // We have RGBA frame from android, Let's convert it to what OpenCV work with.
    // cv::cvtColor(image, image, cv::COLOR_RGBA2BGR);

    cv::Mat blob = prepare_input(image);
    model.setInput(blob);
    std::vector<cv::Mat> outputs;
    model.forward(outputs, layerNames);
    cv::Mat output0 = outputs[0];
    /*
		4 + class_list.size()

		Here 4 is constant. 0-3 is x,y,w,h

		[x, y, w, h, number of classes]

	*/
    output0 = output0.reshape(0, class_list.size() + 4);
    output0 = output0.t();

    std::vector<string> labels = post_process(image, output0, class_list);
    for(string label: labels){
        #ifdef __ANDROID__
                __android_log_print(ANDROID_LOG_INFO, "MyTag", label.c_str());
        #endif
    }


    vector<double> layersTimes;
    double freq = cv::getTickFrequency() / 1000;
    double t = model.getPerfProfile(layersTimes) / freq;
    string label = format("Inference time : %.2f ms", t);
    cv::putText(image, label, Point(20, 40), FONT_FACE, FONT_SCALE, RED);

    return labels;

}

/**
 * The function takes path to model and load it.
 *
 * @param modelPath A std::string having a path where model file is located.
 *
 * @return The function returns True if model is loaded. False if model could not be loaded.
 */
bool loadOnnxModel(std::string modelPath){
    model = cv::dnn::readNetFromONNX(modelPath);
    model.setPreferableBackend(cv::dnn::DNN_BACKEND_DEFAULT);
    model.setPreferableTarget(cv::dnn::DNN_TARGET_OPENCL); // You can change this to DNN_TARGET_VULKAN if needed

    if (model.empty()) {
        std::cerr << "Failed to load the ONNX model." << std::endl;
        return false;
    }
    return true;
}