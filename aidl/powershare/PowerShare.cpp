/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "vendor.lineage.powershare-service.oplus"

#include "PowerShare.h"

#include <android-base/file.h>
#include <android-base/logging.h>
#include <sys/stat.h>

using ::android::base::ReadFileToString;
using ::android::base::WriteStringToFile;

namespace {

constexpr const char* kWirelessTxEnablePath = "/proc/wireless/enable_tx";

}  // anonymous namespace

namespace aidl {
namespace vendor {
namespace lineage {
namespace powershare {

ndk::ScopedAStatus PowerShare::getMinBattery(int32_t* _aidl_return) {
    *_aidl_return = 0;
    return ndk::ScopedAStatus::ok();
}

ndk::ScopedAStatus PowerShare::isEnabled(bool* _aidl_return) {
    struct stat sb;
    if (stat(kWirelessTxEnablePath, &sb) != 0) {
        LOG(WARNING) << "PowerShare node missing, assuming disabled";
        *_aidl_return = false;
        return ndk::ScopedAStatus::ok();
    }

    std::string value;
    if (!ReadFileToString(kWirelessTxEnablePath, &value)) {
        LOG(ERROR) << "Failed to read current PowerShare state";
        *_aidl_return = false;
        return ndk::ScopedAStatus::ok();
    }

    *_aidl_return = value != "disable\n";
    return ndk::ScopedAStatus::ok();
}

ndk::ScopedAStatus PowerShare::setEnabled(bool enable) {
    struct stat sb;
    if (stat(kWirelessTxEnablePath, &sb) != 0) {
        LOG(WARNING) << "Attempted to set PowerShare on a device without support";
        return ndk::ScopedAStatus::ok();
    }
    if (!WriteStringToFile(enable ? "1" : "0", kWirelessTxEnablePath, true)) {
        LOG(ERROR) << "Failed to write PowerShare state";
        return ndk::ScopedAStatus::ok();
    }

    return ndk::ScopedAStatus::ok();
}

ndk::ScopedAStatus PowerShare::setMinBattery(int32_t minBattery) {
    return ndk::ScopedAStatus::ok();
}

}  // namespace powershare
}  // namespace lineage
}  // namespace vendor
}  // namespace aidl
