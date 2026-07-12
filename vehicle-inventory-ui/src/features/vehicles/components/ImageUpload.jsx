import { useState, useRef } from 'react';
import PropTypes from 'prop-types';
import { Upload, X, ImageIcon } from 'lucide-react';
import Button from '../../../components/Button/Button';

function ImageUpload({ currentImageUrl, onFileSelect, onRemove, disabled = false }) {
  const [preview, setPreview] = useState(currentImageUrl || null);
  const [fileName, setFileName] = useState('');
  const inputRef = useRef(null);

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setFileName(file.name);
    const reader = new FileReader();
    reader.onloadend = () => setPreview(reader.result);
    reader.readAsDataURL(file);
    onFileSelect(file);
  };

  const handleRemove = () => {
    setPreview(null);
    setFileName('');
    if (inputRef.current) inputRef.current.value = '';
    onRemove?.();
  };

  return (
    <div className="space-y-3">
      <label className="block text-sm font-medium text-text-secondary">Vehicle Image</label>
      {preview ? (
        <div className="relative w-full max-w-sm rounded-lg overflow-hidden border border-border">
          <img src={preview} alt="Preview" className="w-full h-48 object-cover" />
          {!disabled && (
            <button
              type="button"
              onClick={handleRemove}
              className="absolute top-2 right-2 bg-white/80 rounded-full p-1 hover:bg-white transition-colors cursor-pointer"
              aria-label="Remove image"
            >
              <X size={16} />
            </button>
          )}
          {fileName && <p className="text-xs text-text-muted mt-1 px-2 pb-2">{fileName}</p>}
        </div>
      ) : (
        <div
          className="w-full max-w-sm border-2 border-dashed border-border rounded-lg p-6 flex flex-col items-center justify-center cursor-pointer hover:border-primary transition-colors"
          onClick={() => !disabled && inputRef.current?.click()}
          role="button"
          tabIndex={0}
          onKeyDown={(e) => { if (e.key === 'Enter') inputRef.current?.click(); }}
          aria-label="Upload image"
        >
          <ImageIcon size={32} className="text-text-muted mb-2" />
          <p className="text-sm text-text-secondary">Click to upload an image</p>
          <p className="text-xs text-text-muted mt-1">JPG, PNG, WebP</p>
        </div>
      )}
      <input
        ref={inputRef}
        type="file"
        accept="image/jpeg,image/png,image/webp"
        onChange={handleFileChange}
        className="hidden"
        disabled={disabled}
        aria-label="File input"
      />
      {!preview && !disabled && (
        <Button
          type="button"
          variant="ghost"
          size="sm"
          onClick={() => inputRef.current?.click()}
        >
          <Upload size={16} className="mr-1" /> Choose File
        </Button>
      )}
    </div>
  );
}

ImageUpload.propTypes = {
  currentImageUrl: PropTypes.string,
  onFileSelect: PropTypes.func.isRequired,
  onRemove: PropTypes.func,
  disabled: PropTypes.bool,
};

export default ImageUpload;
