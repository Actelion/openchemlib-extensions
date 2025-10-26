# openchemlib-extensions
Extensions to OpenChemLib

Current Extensions:
- Native Clipbboard Handler via JNA for Windows and MacOS


How it works:
- Build the project and put the resulting jar in the Classpath of your *OpenChemLib* Project (*OpenChemLib* min version CHANGEME) and the extension will be found and used.
  Its best to look at com.actelion.research.gui.clipboard.ClipboardHandler in *OpenChemLib* and JNAWinClipboardHandler in this project to see in detail how things work.
  In short, *OpenChemLib* will try to load the extension classes and if it can not find it, it will fall back to its default implementation.


Purpose:

*OpenChemLib* provides Clipboard functionality like Copy & Paste within its Editors by default. These handlers additionally allows copy & paste from and to ChemDraw via ChemDraw Interchange Format.

