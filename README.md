# IFAS Android VMS — Complete Source V1

Includes Home, Cameras, Playback UI, Recording UI, Cloud UI, Settings, Camera model, manual camera support, ONVIF WS-Discovery, ONVIF SOAP capability/profile/stream-URI service boundary, Media3 RTSP live player, recording repository interface + in-memory implementation, retention engine, cloud gateway interface/config model, and IFAS vector logo.

Important: actual RTSP recording is intentionally not falsely claimed complete. A native RTP/RTSP muxer or FFmpeg backend is required. ONVIF UsernameToken/Digest authentication also needs to be completed for cameras requiring authentication. A real cloud gateway needs the actual IFAS cloud API contract.

Build: `./gradlew assembleDebug`
