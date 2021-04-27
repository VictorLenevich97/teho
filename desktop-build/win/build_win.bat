@echo off
REM teho-1.0-RELEASE.jar и teho.exe (билд electron) должны быть в этой же папке
REM {WIX} - путь к Wix Toolkit
@echo on

set wix_location="C:\Program Files (x86)\WiX Toolset v3.11

%wix_location%\bin\candle.exe" wix_build.wxs -o obj\

%wix_location%\bin\light.exe" obj\wix_build.wixobj -ext WixUIExtension -cultures:ru-RU  -o bin\TEHO_installer.msi

xcopy /Y bin\TEHO_installer.msi TEHO_installer.msi

%wix_location%\bin\candle.exe" wix_build_with_java.wxs -ext WixBalExtension -ext WixUtilExtension -o obj\

%wix_location%\bin\light.exe" obj\wix_build_with_java.wixobj -ext WixUIExtension -cultures:ru-RU  -ext WixBalExtension -o bin\TEHO_installer_w_java.exe