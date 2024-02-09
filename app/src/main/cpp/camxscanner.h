//
// Created by muneeb on 09/02/2024.
//

#ifndef AI_DRIVING_APP_CAMXSCANNER_H
#define AI_DRIVING_APP_CAMXSCANNER_H

#include <string>
#include <opencv2/core.hpp>

std::vector<std::string> processFrame(long matAddr);
bool loadOnnxModel(std::string modelPath);

#endif //AI_DRIVING_APP_CAMXSCANNER_H
