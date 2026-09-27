-- The web app's "attach appeal letter" upload only ever stored the OCR/Tika-
-- extracted text (document.extractedText). The original file (PDF/DOCX/
-- image) was read into memory, used once for text extraction, and then
-- discarded -- there was never any column to keep it in, and appeal.documentID
-- was never even being populated by submitAppeal(). This adds real file
-- storage so prefects can open the original letter from deskPrefect.

ALTER TABLE document ADD FILENAME VARCHAR2(255);
ALTER TABLE document ADD CONTENTTYPE VARCHAR2(100);
ALTER TABLE document ADD FILEDATA BLOB;

COMMIT;
