package android.hardware.fingerprint;

public interface IOplusFingerprintManagerEx {
    public interface OpticalFingerprintListener {
        void onOpticalFingerprintUpdate(int status);
    }
}