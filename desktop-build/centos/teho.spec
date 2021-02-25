%define __jar_repack %{nil}
Name:       teho
Version:    1.0.1
Release:    1
Summary:    TEHO Software
License:    NONE
AutoReqProv: no
              
%description


%prep
cp ~/IdeaProjects/teho-backend/target/teho-1.0-RELEASE.jar $RPM_SOURCE_DIR/teho-1.0-RELEASE.jar
cp ~/IdeaProjects/teho-backend/desktop-build/centos/teho $RPM_SOURCE_DIR/teho
rm -rf $RPM_SOURCE_DIR/frontend
mkdir $RPM_SOURCE_DIR/frontend
cp ~/IdeaProjects/teho-frontend/dist/teho-web-0.1.0.AppImage $RPM_SOURCE_DIR/frontend/teho-web-0.1.0.AppImage
cp ~/IdeaProjects/teho-backend/desktop-build/centos/teho-web.desktop $RPM_SOURCE_DIR/teho-web.desktop
cp ~/IdeaProjects/teho-backend/desktop-build/centos/teho-web.png $RPM_SOURCE_DIR/teho-web.png



%build


%install
rm -rf $RPM_BUILD_ROOT
mkdir -p %{buildroot}/usr/bin/
mkdir -p %{buildroot}/usr/share/applications/
mkdir -p %{buildroot}/usr/share/icons/hicolor/256x256/apps/
mkdir -p %{buildroot}/opt/TEHO/unpacked
install -m 755 $RPM_SOURCE_DIR/teho-1.0-RELEASE.jar %{buildroot}/opt/TEHO/unpacked/teho-1.0-RELEASE.jar
cd %{buildroot}/opt/TEHO/unpacked && jar -xf teho-1.0-RELEASE.jar
rm %{buildroot}/opt/TEHO/unpacked/teho-1.0-RELEASE.jar
install -m 755 $RPM_SOURCE_DIR/teho %{buildroot}/usr/bin/teho
chmod +x %{buildroot}/usr/bin/teho
sudo cp -r $RPM_SOURCE_DIR/frontend %{buildroot}/opt/TEHO
cp $RPM_SOURCE_DIR/teho-web.desktop %{buildroot}/usr/share/applications/teho-web.desktop
cp $RPM_SOURCE_DIR/teho-web.png %{buildroot}/usr/share/icons/hicolor/256x256/apps/teho-web.png


%files
/usr/bin/teho
/opt/TEHO/*
/usr/share/applications/teho-web.desktop
/usr/share/icons/hicolor/256x256/apps/teho-web.png

%doc

%changelog
