%define __jar_repack %{nil}
Name:       teho
Version:    %VERSION%
Release:    1
Summary:    TEHO Software
License:    NONE
AutoProv: no
AutoReq: no
AutoReqProv: no

%description


%prep
cp %BACKENDPATH%/target/teho-1.0-RELEASE.jar $RPM_SOURCE_DIR/teho-1.0-RELEASE.jar
cp %BACKENDPATH%/desktop-build/centos/teho $RPM_SOURCE_DIR/teho
rm -rf $RPM_SOURCE_DIR/frontend
mkdir $RPM_SOURCE_DIR/frontend
cp %FRONTENDPATH%/dist/%FRONTEND_NAME%-%FRONTEND_VERSION%.AppImage $RPM_SOURCE_DIR/frontend/teho-web.AppImage
cp %BACKENDPATH%/desktop-build/centos/teho-web.desktop $RPM_SOURCE_DIR/teho-web.desktop
cp %BACKENDPATH%/desktop-build/centos/teho-web*.png $RPM_SOURCE_DIR/



%build


%install
rm -rf $RPM_BUILD_ROOT
mkdir -p %{buildroot}/usr/bin/
mkdir -p %{buildroot}/usr/share/applications/

mkdir -p %{buildroot}/usr/share/icons/hicolor/16x16/apps/
mkdir -p %{buildroot}/usr/share/icons/hicolor/32x32/apps/
mkdir -p %{buildroot}/usr/share/icons/hicolor/48x48/apps/
mkdir -p %{buildroot}/usr/share/icons/hicolor/64x64/apps/
mkdir -p %{buildroot}/usr/share/icons/hicolor/128x128/apps/
mkdir -p %{buildroot}/usr/share/icons/hicolor/256x256/apps/
mkdir -p %{buildroot}/usr/share/icons/hicolor/512x512/apps/

mkdir -p %{buildroot}/opt/TEHO/unpacked
install -m 755 $RPM_SOURCE_DIR/teho-1.0-RELEASE.jar %{buildroot}/opt/TEHO/unpacked/teho-1.0-RELEASE.jar
cd %{buildroot}/opt/TEHO/unpacked && jar -xf teho-1.0-RELEASE.jar
rm %{buildroot}/opt/TEHO/unpacked/teho-1.0-RELEASE.jar
install -m 755 $RPM_SOURCE_DIR/teho %{buildroot}/usr/bin/teho
chmod +x %{buildroot}/usr/bin/teho
sudo cp -r $RPM_SOURCE_DIR/frontend %{buildroot}/opt/TEHO
cp $RPM_SOURCE_DIR/teho-web.desktop %{buildroot}/usr/share/applications/teho-web.desktop
cp $RPM_SOURCE_DIR/teho-web_16x16.png %{buildroot}/usr/share/icons/hicolor/16x16/apps/teho-web.png
cp $RPM_SOURCE_DIR/teho-web_32x32.png %{buildroot}/usr/share/icons/hicolor/32x32/apps/teho-web.png
cp $RPM_SOURCE_DIR/teho-web_48x48.png %{buildroot}/usr/share/icons/hicolor/48x48/apps/teho-web.png
cp $RPM_SOURCE_DIR/teho-web_64x64.png %{buildroot}/usr/share/icons/hicolor/64x64/apps/teho-web.png
cp $RPM_SOURCE_DIR/teho-web_128x128.png %{buildroot}/usr/share/icons/hicolor/128x128/apps/teho-web.png
cp $RPM_SOURCE_DIR/teho-web_256x256.png %{buildroot}/usr/share/icons/hicolor/256x256/apps/teho-web.png
cp $RPM_SOURCE_DIR/teho-web_512x512.png %{buildroot}/usr/share/icons/hicolor/512x512/apps/teho-web.png

%post
sudo touch /usr/share/icons/hicolor
sudo gtk-update-icon-cache


%files
/usr/bin/teho
/opt/TEHO/*
/usr/share/applications/teho-web.desktop
/usr/share/icons/hicolor/16x16/apps/teho-web.png
/usr/share/icons/hicolor/32x32/apps/teho-web.png
/usr/share/icons/hicolor/48x48/apps/teho-web.png
/usr/share/icons/hicolor/64x64/apps/teho-web.png
/usr/share/icons/hicolor/128x128/apps/teho-web.png
/usr/share/icons/hicolor/256x256/apps/teho-web.png
/usr/share/icons/hicolor/512x512/apps/teho-web.png

%doc

%changelog
