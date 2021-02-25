#teho-1.0-RELEASE.jar и teho.exe (билд electron) должны быть в этой же папке
#{WIX} - путь к Wix Toolkit

{WIX}\bin\candle.exe" *.wxs -o obj\

{WIX}\bin\light.exe" obj\*.wixobj -ext WixUIExtension -cultures:ru-RU  -o bin\TEHO_installer.msi