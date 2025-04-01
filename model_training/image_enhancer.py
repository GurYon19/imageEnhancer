import torch
import torch.nn as nn
import torchvision.models as models
import numpy as np
import pywt

class ImageAnalyzer(nn.Module):
    def __init__(self):
        super(ImageAnalyzer, self).__init__()
        # Load pretrained MobileNetV3-Large
        self.mobilenet = models.mobilenet_v3_large(pretrained=True)
        # Remove the classifier to use as feature extractorD
        self.mobilenet.classifier = nn.Identity()

    def analyze_image(self, x):
        """
        Extract image quality metrics using MobileNetV3 features and wavelet transform
        """
        # Get features from MobileNetV3
        with torch.no_grad():
            features = self.mobilenet(x)

        # Convert input tensor to numpy for wavelet transform
        img = x.squeeze(0).permute(1, 2, 0).cpu().numpy()

        # Convert to grayscale for wavelet transform
        gray = np.mean(img, axis=2)

        # Apply wavelet transform
        coeffs = pywt.wavedec2(gray, 'haar', level=2)
        cA, (cH1, cV1, cD1), (cH2, cV2, cD2) = coeffs

        # Calculate sharpness from wavelet coefficients
        h_energy = np.sum(cH1**2) + np.sum(cH2**2)
        v_energy = np.sum(cV1**2) + np.sum(cV2**2)
        sharpness = np.sqrt((h_energy + v_energy) / (cH1.size + cH2.size + cV1.size + cV2.size)) * 20.0
        sharpness = min(1.0, sharpness)

        # Calculate noise level from diagonal coefficients
        noise = np.sqrt(np.sum(cD1**2) / cD1.size) * 30.0
        noise = min(1.0, noise)

        # Calculate contrast and brightness
        contrast = np.std(gray) / 128.0
        brightness = np.mean(gray) / 255.0

        # Return metrics dictionary
        return {
            "sharpness": float(sharpness),
            "noise_level": float(noise),
            "contrast": float(contrast),
            "brightness": float(brightness)
        }

    def forward(self, x):
        return x

# Export model with GPU support
def export_model_with_gpu_support():
    model = ImageAnalyzer()
    model.eval()

    # Sample input for tracing
    example_input = torch.randn(1, 3, 224, 224)

    # Export the model with optimizations for mobile GPU
    # Use script instead of trace for better GPU compatibility
    scripted_model = torch.jit.script(model)

    # Set mobile GPU optimizations
    scripted_model_optimized = optimize_for_mobile(scripted_model)

    # Save the model
    scripted_model_optimized._save_for_lite_interpreter("image_analyzer_gpu.pt")
    print("GPU-optimized model exported to image_analyzer_gpu.pt")

# Function to optimize model for mobile
def optimize_for_mobile(scripted_model):
    from torch.utils.mobile_optimizer import optimize_for_mobile
    return optimize_for_mobile(scripted_model)

if __name__ == "__main__":
    export_model_with_gpu_support()