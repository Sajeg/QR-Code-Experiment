## QR Code Experiment

This is a Jetpack Compose Android app for transferring large strings between devices via QR Code. It was a fun project to create and will remain a small-scale application.

## Branches

There are two branches, each utilizing different QR code scanning methods. Scanning with ML-Kit is highly recommended due to its significantly faster performance.

## How it works

1. **String Chunking:** The string to be transferred is divided into chunks of 150 characters.
2. **Chunked Transmission:**  Every `50ms / 100ms / 150ms` (configurable), a chunk is transmitted along with a metadata string in the following format: `[5/9]`.  The first number represents the index of the current chunk, and the second number indicates the total number of chunks.
3. **QR Code Scanning:** The QR code scanner decodes the metadata and places the received chunk in the appropriate position within a list.
4. **Reconstruction:** Once the list receives all chunks, the string is reconstructed and returned. 
