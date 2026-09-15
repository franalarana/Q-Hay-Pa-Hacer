import React, { useState, useRef, useEffect } from 'react';
import { ChevronDown } from 'lucide-react';

export default function CustomSelect({ value, onChange, options, placeholder = "Selecciona..." }) {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const selectedOption = options.find(opt => opt.value === value);

  return (
    <div ref={dropdownRef} style={{ position: 'relative', width: '100%' }}>
      <div 
        className={`pastel-input ${isOpen ? 'open' : ''}`}
        style={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          borderColor: isOpen ? 'var(--primary-dark)' : '#C0E4DC',
          boxShadow: isOpen ? '0 0 0 3px rgba(108, 175, 161, 0.2)' : '0 2px 4px rgba(0,0,0,0.02)',
          userSelect: 'none'
        }}
        onClick={() => setIsOpen(!isOpen)}
      >
        <span style={{ 
          color: selectedOption ? 'var(--text-main)' : 'var(--text-muted)',
          display: 'flex',
          alignItems: 'center',
          gap: '8px'
        }}>
          {selectedOption ? selectedOption.label : placeholder}
        </span>
        <ChevronDown size={16} color="var(--text-muted)" style={{ transform: isOpen ? 'rotate(180deg)' : 'none', transition: 'transform 0.2s ease' }} />
      </div>

      {isOpen && (
        <div style={{
          position: 'absolute',
          top: '100%',
          left: 0,
          right: 0,
          marginTop: '6px',
          backgroundColor: '#fff',
          borderRadius: '12px',
          border: '1px solid #E5E7EB',
          boxShadow: '0 10px 25px -5px rgba(0, 0, 0, 0.1)',
          maxHeight: '260px',
          overflowY: 'auto',
          zIndex: 50,
          padding: '6px'
        }} className="custom-scrollbar">
          {options.map((opt, idx) => (
            <div 
              key={idx}
              onClick={() => {
                onChange(opt.value);
                setIsOpen(false);
              }}
              className="custom-select-option"
              style={{
                backgroundColor: opt.value === value ? 'var(--pastel-mint)' : 'transparent',
                fontWeight: opt.value === value ? '600' : '400',
                borderBottom: opt.isDivider ? '1px solid #E5E7EB' : 'none',
                marginBottom: opt.isDivider ? '4px' : '0'
              }}
            >
              {opt.label}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
