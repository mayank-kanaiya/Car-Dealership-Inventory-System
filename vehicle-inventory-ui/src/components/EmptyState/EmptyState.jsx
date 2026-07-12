import PropTypes from 'prop-types';
import { Inbox } from 'lucide-react';
import Button from '../Button/Button';

function EmptyState({ title, description, actionLabel, onAction, icon }) {
  return (
    <div className="flex flex-col items-center justify-center py-12 px-4 text-center">
      <div className="mb-4 text-text-muted">
        {icon || <Inbox size={48} strokeWidth={1.5} />}
      </div>
      <h3 className="text-lg font-semibold text-text-primary mb-1">{title}</h3>
      {description && (
        <p className="text-sm text-text-secondary max-w-sm mb-4">{description}</p>
      )}
      {actionLabel && onAction && (
        <Button onClick={onAction}>{actionLabel}</Button>
      )}
    </div>
  );
}

EmptyState.propTypes = {
  title: PropTypes.string.isRequired,
  description: PropTypes.string,
  actionLabel: PropTypes.string,
  onAction: PropTypes.func,
  icon: PropTypes.node,
};

export default EmptyState;
