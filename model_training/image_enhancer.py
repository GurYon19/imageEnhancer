import torch
import torch.nn as nn
import numpy as np
import pywt
from torchvision import transforms


class ImageEnhancer(nn.Module):
